package com.superlawva.global.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class S3LogAppender extends AppenderBase<ILoggingEvent> {

    @Value("${aws.s3.bucket-name:superlawva-logs}")
    private String bucketName;

    @Value("${aws.s3.region:ap-northeast-2}")
    private String region;

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    private S3Client s3Client;
    private BlockingQueue<String> logBuffer;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> uploadTask;
    private static final int BUFFER_SIZE = 100;
    private static final int UPLOAD_INTERVAL_SECONDS = 60;

    @Override
    public void start() {
        if (!isStarted()) {
            initializeS3Client();
            initializeBuffer();
            startUploadScheduler();
            super.start();
        }
    }

    @Override
    public void stop() {
        if (isStarted()) {
            uploadRemainingLogs();
            if (uploadTask != null) {
                uploadTask.cancel(false);
            }
            if (scheduler != null) {
                scheduler.shutdown();
            }
            super.stop();
        }
    }

    private void initializeS3Client() {
        try {
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .build();
        } catch (Exception e) {
            addError("Failed to initialize S3 client", e);
        }
    }

    private void initializeBuffer() {
        logBuffer = new LinkedBlockingQueue<>(BUFFER_SIZE);
    }

    private void startUploadScheduler() {
        scheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
        uploadTask = scheduler.scheduleAtFixedRate(
                this::uploadBufferedLogs,
                UPLOAD_INTERVAL_SECONDS,
                UPLOAD_INTERVAL_SECONDS,
                TimeUnit.SECONDS
        );
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }

        try {
            String logEntry = formatLogEntry(event);
            if (!logBuffer.offer(logEntry)) {
                // 버퍼가 가득 찬 경우 즉시 업로드
                uploadBufferedLogs();
                logBuffer.offer(logEntry);
            }
        } catch (Exception e) {
            addError("Failed to append log event", e);
        }
    }

    private String formatLogEntry(ILoggingEvent event) {
        return String.format("%s [%s] %s %s - %s%n",
                event.getTimeStamp(),
                event.getThreadName(),
                event.getLevel(),
                event.getLoggerName(),
                event.getFormattedMessage()
        );
    }

    private void uploadBufferedLogs() {
        if (logBuffer.isEmpty()) {
            return;
        }

        try {
            StringBuilder logs = new StringBuilder();
            String logEntry;
            int count = 0;

            while ((logEntry = logBuffer.poll()) != null && count < BUFFER_SIZE) {
                logs.append(logEntry);
                count++;
            }

            if (logs.length() > 0) {
                uploadToS3(logs.toString());
            }
        } catch (Exception e) {
            addError("Failed to upload buffered logs", e);
        }
    }

    private void uploadToS3(String logs) {
        try {
            String key = generateS3Key();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType("text/plain")
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(
                    new ByteArrayInputStream(logs.getBytes(StandardCharsets.UTF_8)),
                    logs.length()
            ));

            addInfo("Successfully uploaded " + logs.length() + " bytes to S3: " + key);
        } catch (Exception e) {
            addError("Failed to upload logs to S3", e);
        }
    }

    private String generateS3Key() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH"));
        String hostname = System.getProperty("user.name", "unknown");
        return String.format("logs/%s/%s-%s.log", timestamp, hostname, System.currentTimeMillis());
    }

    private void uploadRemainingLogs() {
        uploadBufferedLogs();
    }
} 