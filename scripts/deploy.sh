#!/bin/bash
set -e

echo "=== EC2 asdf확인 ==="
ssh -o StrictHostKeyChecking=no ubuntu@$EC2_HOST << 'EOF'
echo "Memory:"
free -h
echo "Disk:"
df -h
echo "Load:"
uptime
EOF

echo "=== Copying JAR (10 min timeout) ==="
scp -o StrictHostKeyChecking=no build/libs/superlawva-0.0.1-SNAPSHOT.jar ubuntu@$EC2_HOST:/home/ubuntu/app.jar.new

echo "=== Copying docker-compose file ==="
scp -o StrictHostKeyChecking=no docker-compose.redis.yml ubuntu@$EC2_HOST:/home/ubuntu/

echo "=== 원격 배포 스크립트 작성 ==="
ssh -o StrictHostKeyChecking=no ubuntu@$EC2_HOST << 'EOF'
echo '=== Java 설치 확인 ==='
java -version

echo '=== Docker 설치 확인 ==='
docker --version

echo '=== Redis 재시작 ==='
cd /home/ubuntu
if command -v docker-compose >/dev/null 2>&1; then
    echo 'docker-compose 사용'
    docker-compose -f docker-compose.redis.yml down
    docker-compose -f docker-compose.redis.yml up -d
else
    echo 'docker compose 사용'
    docker compose -f docker-compose.redis.yml down
    docker compose -f docker-compose.redis.yml up -d
fi

echo '=== 데이터베이스 마이그레이션 ==='
# MySQL 클라이언트 설치 확인 및 설치
if ! command -v mysql >/dev/null 2>&1; then
    echo 'MySQL 클라이언트 설치 중...'
    sudo apt-get update
    sudo apt-get install -y mysql-client
fi

# 데이터베이스 연결 정보 추출
DB_URL=$(echo $DATABASE_URL | sed 's/jdbc:mysql:\/\///')
DB_HOST=$(echo $DB_URL | cut -d'/' -f1 | cut -d':' -f1)
DB_PORT=$(echo $DB_URL | cut -d'/' -f1 | cut -d':' -f2)
DB_NAME=$(echo $DB_URL | cut -d'/' -f2)

echo "데이터베이스 연결 정보:"
echo "Host: $DB_HOST"
echo "Port: $DB_PORT"
echo "Database: $DB_NAME"

# deleted_at 컬럼 추가
mysql -h $DB_HOST -P $DB_PORT -u $DB_USERNAME -p$DB_PASSWORD $DB_NAME << 'SQL_EOF'
-- users 테이블에 deleted_at 컬럼 추가 (이미 존재하면 무시)
ALTER TABLE users ADD COLUMN deleted_at DATETIME NULL;
SQL_EOF

echo '=== JAR 교체 ==='
if [ -f /home/ubuntu/app.jar.new ]; then
    mv /home/ubuntu/app.jar.new /home/ubuntu/app.jar
fi

echo '=== 기존 앱 중지 ==='
sudo fuser -k 8080/tcp || echo "8080 포트 사용 프로세스가 없거나 종료에 실패했습니다 (계속 진행)"
sleep 5

echo '=== 환경변수 확인 ==='
echo "DATABASE_URL 형식 확인: $(echo $DATABASE_URL | sed 's/\/\/.*@/\/\/***@/g')"
echo "DB_USERNAME: $DB_USERNAME"
echo "DB_PASSWORD: $(echo $DB_PASSWORD | sed 's/./*/g')"
echo "REDIS_HOST: $REDIS_HOST"
echo "REDIS_PORT: $REDIS_PORT"

echo '=== 앱 시작 ==='
cd /home/ubuntu
nohup java -jar app.jar > app.log 2>&1 &
echo "앱이 백그라운드에서 시작되었습니다. PID: $!"

echo '=== 앱 상태 확인 ==='
sleep 10
if curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
    echo "✅ 앱이 정상적으로 시작되었습니다!"
    echo "헬스체크 결과:"
    curl -s http://localhost:8080/actuator/health | jq . || curl -s http://localhost:8080/actuator/health
else
    echo "❌ 앱 시작에 실패했습니다. 로그를 확인하세요:"
    tail -20 app.log
    exit 1
fi

echo '=== 최종 상태 확인 ==='
echo "메모리 사용량:"
free -h
echo "디스크 사용량:"
df -h
echo "8080 포트 사용 프로세스:"
sudo netstat -tlnp | grep :8080 || echo "8080 포트에서 실행 중인 프로세스가 없습니다"
EOF

echo "✅ 배포가 완료되었습니다!" 