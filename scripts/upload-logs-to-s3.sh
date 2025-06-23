#!/bin/bash

# 로그를 S3로 업로드하는 스크립트
# cron job으로 실행: 0 */6 * * * /path/to/upload-logs-to-s3.sh

# 설정
S3_BUCKET="superlawva-logs"
LOG_DIR="/var/log/superlawva"
REGION="ap-northeast-2"
DATE=$(date +%Y-%m-%d)
HOSTNAME=$(hostname)

# AWS CLI가 설치되어 있는지 확인
if ! command -v aws &> /dev/null; then
    echo "AWS CLI is not installed. Please install it first."
    exit 1
fi

# 로그 디렉토리가 존재하는지 확인
if [ ! -d "$LOG_DIR" ]; then
    echo "Log directory $LOG_DIR does not exist."
    exit 1
fi

# 오늘 날짜의 로그 파일들을 S3로 업로드
upload_logs() {
    local log_type=$1
    local pattern=$2
    
    for file in $LOG_DIR/$pattern; do
        if [ -f "$file" ]; then
            # 파일명에서 날짜 추출
            file_date=$(basename "$file" | grep -o '[0-9]\{4\}-[0-9]\{2\}-[0-9]\{2\}' || echo "$DATE")
            
            # S3 키 생성
            s3_key="logs/$file_date/$HOSTNAME/$(basename "$file")"
            
            echo "Uploading $file to s3://$S3_BUCKET/$s3_key"
            
            # S3로 업로드
            aws s3 cp "$file" "s3://$S3_BUCKET/$s3_key" --region "$REGION"
            
            if [ $? -eq 0 ]; then
                echo "Successfully uploaded $file"
                # 업로드 성공 후 로컬 파일 삭제 (선택사항)
                # rm "$file"
            else
                echo "Failed to upload $file"
            fi
        fi
    done
}

# 메인 실행
echo "Starting log upload to S3 at $(date)"

# 일반 로그 파일 업로드
upload_logs "application" "application-*.log"

# 에러 로그 파일 업로드
upload_logs "error" "error-*.log"

# SQL 로그 파일 업로드 (있는 경우)
upload_logs "sql" "sql-*.log"

echo "Log upload completed at $(date)" 