package com.superlawva.global.config;

<<<<<<< HEAD
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ResourceUtils;

=======
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.DocumentProcessorServiceSettings;
<<<<<<< HEAD

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class GcpConfig {

    @Value("${gcp.project-id}")
    private String projectId;

    @Value("${gcp.location}")
    private String location;

    @Value("${gcp.processor-id}")
    private String processorId;

    @Value("${gcp.credentials.path:#{null}}")
    private String credentialsPath;
//
//    public String getProjectId() {
//        return projectId;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public String getProcessorId() {
//        return processorId;
//    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "gcp.enabled", havingValue = "true")
    public GoogleCredentials googleCredentials() throws IOException {
        log.info("GCP 인증 정보 로드 중...");
        GoogleCredentials credentials;
        
        if (credentialsPath != null && !credentialsPath.isEmpty()) {
            log.info("지정된 경로에서 GCP 인증 정보 파일 로드: {}", credentialsPath);
            try (InputStream in = new FileInputStream(ResourceUtils.getFile(credentialsPath))) {
                credentials = GoogleCredentials.fromStream(in);
            }
        } else {
            log.info("기본 Application Default Credentials 사용");
            credentials = GoogleCredentials.getApplicationDefault();
        }
        
        // Document AI API에 필요한 스코프 추가
        return credentials.createScoped("https://www.googleapis.com/auth/cloud-platform");
    }

    @Bean
    @ConditionalOnProperty(name = "gcp.enabled", havingValue = "true")
    public DocumentProcessorServiceClient documentProcessorServiceClient(GoogleCredentials credentials) throws IOException {
        try {
            // us-central1 -> us-documentai.googleapis.com
            String endpointRegion = location.startsWith("us") ? "us" : location;
            String endpoint = String.format("%s-documentai.googleapis.com:443", endpointRegion);
            log.info("Document AI 클라이언트 생성 중 - Endpoint: {}, Project: {}, Processor: {}", 
                    endpoint, projectId, processorId);
            
            DocumentProcessorServiceSettings settings = DocumentProcessorServiceSettings.newBuilder()
                    .setEndpoint(endpoint)
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();
            
            DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create(settings);
            log.info("Document AI 클라이언트 생성 성공");
            return client;
            
        } catch (Exception e) {
            log.error("Document AI 클라이언트 생성 실패: {}", e.getMessage(), e);
            throw new IOException("Document AI 클라이언트 생성 실패: " + e.getMessage(), e);
        }
=======
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GcpConfig {

    @Value("${gcp.credentials.path}")
    private Resource credentialsPath;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        // Document AI를 포함한 모든 GCP 서비스에 대한 권한 범위(Scope)
        final String cloudPlatformScope = "https://www.googleapis.com/auth/cloud-platform";

        try {
            // 1. 배포 환경을 위한 ADC(Application Default Credentials) 시도
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            System.out.println("[GCP] GoogleCredentials loaded successfully via ADC.");
            // 로드된 인증 정보에 명시적으로 권한 범위를 부여하여 반환
            return credentials.createScoped(cloudPlatformScope);
        } catch (IOException e) {
            // 2. ADC 실패 시, 로컬 환경을 위한 Classpath Resource 에서 로드
            System.out.println("[GCP] ADC failed. Falling back to classpath resource: " + credentialsPath.getFilename());
            if (credentialsPath == null || !credentialsPath.exists()) {
                throw new FileNotFoundException(
                        "GCP credential file not found at classpath:" + credentialsPath.getFilename() +
                        ", and ADC are not configured."
                );
            }
            try (InputStream inputStream = credentialsPath.getInputStream()) {
                // 파일에서 읽은 인증 정보에 명시적으로 권한 범위를 부여하여 반환
                GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                        .createScoped(cloudPlatformScope);
                System.out.println("[GCP] GoogleCredentials loaded and scoped from " + credentialsPath.getFilename());
                return credentials;
            }
        }
    }

    @Bean
    public DocumentProcessorServiceClient documentProcessorServiceClient(GoogleCredentials credentials) throws IOException {
        DocumentProcessorServiceSettings settings = DocumentProcessorServiceSettings.newBuilder()
                .setEndpoint("us-documentai.googleapis.com:443")
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        return DocumentProcessorServiceClient.create(settings);
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
    }
}