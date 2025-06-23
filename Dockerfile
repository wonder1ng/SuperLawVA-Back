# stage 1: builder
FROM gradle:8.5.0-jdk17 AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --no-daemon

# stage 2: runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar

# 환경변수 기본값 설정 (운영환경에서는 실제 값으로 override)
ENV JWT_SECRET=default_secret_for_docker_only_please_change_in_production
ENV JWT_ACCESS_TOKEN_VALIDITY=43200000
ENV DATABASE_URL=jdbc:mysql://localhost:3306/superlawva
ENV DB_USERNAME=root
ENV DB_PASSWORD=password
ENV REDIS_HOST=localhost
ENV REDIS_PORT=6379
ENV MAIL_USERNAME=your-email@gmail.com
ENV MAIL_PASSWORD=your-app-password
ENV KAKAO_CLIENT_ID=kakao_client_id
ENV KAKAO_CLIENT_SECRET=kakao_client_secret
ENV NAVER_CLIENT_ID=naver_client_id
ENV NAVER_CLIENT_SECRET=naver_client_secret
ENV FRONTEND_URL=http://localhost:3000
ENV SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
