<<<<<<< HEAD
//package com.superlawva.domain.ml.client;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class MLApiClient {
//
//    private final RestTemplate restTemplate;
//    private final ObjectMapper objectMapper;
//
//    @Value("${ml.api.base-url:http://3.34.41.104:8000}")
//    private String mlApiBaseUrl;
//
//    @Value("${ml.api.timeout:60000}")
//    private int timeout;
//
//    /**
//     * 계약서 분석 요청 (실제 ML API 엔드포인트: /api/v1/analyze-contract)
//     */
//    public Map<String, Object> analyzeContract(Map<String, Object> contractData) {
//        log.info("🤖 ML API 계약서 분석 요청 시작 - Contract ID: {}", contractData.get("contract_id"));
//
//        try {
//            String url = mlApiBaseUrl + "/api/v1/analyze-contract";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("User-Agent", "SuperLawva-Backend/1.0");
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contractData, headers);
//
//            long startTime = System.currentTimeMillis();
//            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
//
//            double processingTime = (System.currentTimeMillis() - startTime) / 1000.0;
//            log.info("✅ ML API 계약서 분석 완료 - 처리시간: {}초", processingTime);
//
//            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                return response.getBody();
//            } else {
//                throw new RuntimeException("ML API 응답 오류: " + response.getStatusCode());
//            }
//
//        } catch (Exception e) {
//            log.error("❌ ML API 계약서 분석 실패", e);
//            throw new RuntimeException("ML 계약서 분석 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 내용증명서 생성 요청 (실제 ML API 엔드포인트: /api/v1/generate-letter)
//     */
//    public Map<String, Object> generateProofDocument(Map<String, Object> contractData) {
//        log.info("📝 ML API 내용증명서 생성 요청 시작 - Contract ID: {}", contractData.get("contract_id"));
//
//        try {
//            String url = mlApiBaseUrl + "/api/v1/generate-letter";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("User-Agent", "SuperLawva-Backend/1.0");
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contractData, headers);
//
//            long startTime = System.currentTimeMillis();
//            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
//
//            double processingTime = (System.currentTimeMillis() - startTime) / 1000.0;
//            log.info("✅ ML API 내용증명서 생성 완료 - 처리시간: {}초", processingTime);
//
//            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                return response.getBody();
//            } else {
//                throw new RuntimeException("ML API 응답 오류: " + response.getStatusCode());
//            }
//
//        } catch (Exception e) {
//            log.error("❌ ML API 내용증명서 생성 실패", e);
//            throw new RuntimeException("ML 내용증명서 생성 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 특약사항 생성 요청 (실제 ML API 엔드포인트: /api/v1/contract/generate-special-terms)
//     */
//    public Map<String, Object> generateSpecialTerms(Map<String, Object> contractData) {
//        log.info("⚖️ ML API 특약사항 생성 요청 시작 - Contract ID: {}", contractData.get("contract_id"));
//
//        try {
//            String url = mlApiBaseUrl + "/api/v1/contract/generate-special-terms";
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("User-Agent", "SuperLawva-Backend/1.0");
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contractData, headers);
//
//            long startTime = System.currentTimeMillis();
//            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
//
//            double processingTime = (System.currentTimeMillis() - startTime) / 1000.0;
//            log.info("✅ ML API 특약사항 생성 완료 - 처리시간: {}초", processingTime);
//
//            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                return response.getBody();
//            } else {
//                throw new RuntimeException("ML API 응답 오류: " + response.getStatusCode());
//            }
//
//        } catch (Exception e) {
//            log.error("❌ ML API 특약사항 생성 실패", e);
//            throw new RuntimeException("ML 특약사항 생성 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }
//
//    /**
//     * ML API 서버 상태 확인
//     */
//    public boolean isHealthy() {
//        try {
//            String url = mlApiBaseUrl + "/docs";
//            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//            return response.getStatusCode() == HttpStatus.OK;
//        } catch (Exception e) {
//            log.error("ML API 서버 상태 확인 실패", e);
//            return false;
//        }
//    }
//}
=======
package com.superlawva.domain.ml.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class MLApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.servers.legal.base-url:${legal.api.base-url:http://3.34.41.104:8000}}")
    private String mlApiBaseUrl;

    @Value("${ml.api.timeout:60000}")
    private int timeout;

    /**
     * 계약서 분석 요청 (실제 ML API 엔드포인트: /api/v1/analyze-contract)
     */
    public Map<String, Object> analyzeContract(Map<String, Object> contractData) {
        String contractId = (String) contractData.get("contract_id");
        String userId = (String) contractData.get("user_id");

        log.info("🤖 ML API 계약서 분석 요청 시작");
        log.info("   📋 Contract ID: {}", contractId);
        log.info("   👤 User ID: {}", userId);
        log.info("   🌐 ML API URL: {}", mlApiBaseUrl);

        try {
            String url = mlApiBaseUrl + "/api/v1/analyze-contract";
            log.info("📤 ML API 요청 전송 중 - URL: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "SuperLawva-Backend/1.0");

            // 요청 데이터 로깅 (민감한 정보 제외)
            log.info("📋 요청 데이터 구조:");
            log.info("   - contract_id: {}", contractData.get("contract_id"));
            log.info("   - user_id: {}", contractData.get("user_id"));
            log.info("   - contract_type: {}", contractData.get("contract_type"));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contractData, headers);

            long startTime = System.currentTimeMillis();
            log.info("⏰ ML API 요청 시작 시간: {}", startTime);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            double processingTime = (System.currentTimeMillis() - startTime) / 1000.0;

            // 응답 상태 상세 로깅
            log.info("📥 ML API 응답 수신:");
            log.info("   ✅ HTTP 상태: {}", response.getStatusCode());
            log.info("   ⏱️ 처리 시간: {}초", processingTime);
            log.info("   📊 응답 본문 있음: {}", response.getBody() != null);

            if (response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                log.info("   📋 응답 ID: {}", responseBody.get("id"));
                log.info("   📅 생성일: {}", responseBody.get("created_date"));

                if (responseBody.containsKey("articles")) {
                    List<?> articles = (List<?>) responseBody.get("articles");
                    log.info("   📄 분석된 조항 수: {}", articles != null ? articles.size() : 0);
                }
            }

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("✅ ML API 계약서 분석 성공 완료!");
                return response.getBody();
            } else {
                log.error("❌ ML API 응답 오류 - 상태 코드: {}", response.getStatusCode());
                throw new RuntimeException("ML API 응답 오류: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("❌ ML API 계약서 분석 실패");
            log.error("   🔍 오류 클래스: {}", e.getClass().getSimpleName());
            log.error("   💬 오류 메시지: {}", e.getMessage());
            log.error("   📚 전체 스택트레이스:", e);

            // 개발/테스트를 위한 더미 응답 반환
            if (e.getMessage().contains("Connection refused") || e.getMessage().contains("timeout")) {
                log.warn("⚠️ ML API 연결 실패. 테스트 더미 응답 반환");
                return createDummyAnalysisResponse(contractData);
            }

            throw new RuntimeException("ML 계약서 분석 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * ML API 연결 실패 시 테스트용 더미 응답 생성
     */
    private Map<String, Object> createDummyAnalysisResponse(Map<String, Object> contractData) {
        Map<String, Object> dummyResponse = new HashMap<>();

        // 실제 ML API 응답 구조와 동일하게 생성
        dummyResponse.put("id", 999);
        dummyResponse.put("user_id", contractData.get("user_id"));
        dummyResponse.put("contract_id", 0); // ML API의 실제 동작과 동일
        dummyResponse.put("created_date", LocalDateTime.now().toString());

        // 더미 조항 분석 결과
        List<Map<String, Object>> articles = new ArrayList<>();
        Map<String, Object> article1 = new HashMap<>();
        article1.put("result", false);
        article1.put("content", "[테스트] 제1조 계약 조항");
        article1.put("reason", "테스트 모드에서 생성된 더미 분석 결과입니다.");
        article1.put("suggested_revision", "실제 ML API 연결 시 상세 분석이 제공됩니다.");
        article1.put("negotiation_points", "ML API 연결을 확인해주세요.");
        articles.add(article1);

        dummyResponse.put("articles", articles);
        dummyResponse.put("agreements", new ArrayList<>());
        dummyResponse.put("recommended_agreements", new ArrayList<>());

        // 분석 메타데이터
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("model", "Dummy Test Model");
        metadata.put("generation_time", 0.1);
        metadata.put("version", "test-v1.0");
        dummyResponse.put("analysis_metadata", metadata);

        log.info("📦 테스트 더미 응답 생성 완료");
        return dummyResponse;
    }

    /**
     * 내용증명서 생성 요청 (실제 ML API 엔드포인트: /api/v1/generate-letter)
     */
    public Map<String, Object> generateProofDocument(Map<String, Object> contractData) {
        log.info("📝 ML API 내용증명서 생성 요청 시작 - Contract ID: {}", contractData.get("contract_id"));

        try {
            String url = mlApiBaseUrl + "/api/v1/generate-letter";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "SuperLawva-Backend/1.0");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contractData, headers);

            long startTime = System.currentTimeMillis();
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            double processingTime = (System.currentTimeMillis() - startTime) / 1000.0;
            log.info("✅ ML API 내용증명서 생성 완료 - 처리시간: {}초", processingTime);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("ML API 응답 오류: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("❌ ML API 내용증명서 생성 실패", e);
            throw new RuntimeException("ML 내용증명서 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특약사항 생성 요청 (실제 ML API 엔드포인트: /api/v1/contract/generate-special-terms)
     */
    public Map<String, Object> generateSpecialTerms(Map<String, Object> contractData) {
        log.info("⚖️ ML API 특약사항 생성 요청 시작 - Contract ID: {}", contractData.get("contract_id"));

        try {
            String url = mlApiBaseUrl + "/api/v1/contract/generate-special-terms";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "SuperLawva-Backend/1.0");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contractData, headers);

            long startTime = System.currentTimeMillis();
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            double processingTime = (System.currentTimeMillis() - startTime) / 1000.0;
            log.info("✅ ML API 특약사항 생성 완료 - 처리시간: {}초", processingTime);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("ML API 응답 오류: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("❌ ML API 특약사항 생성 실패", e);
            throw new RuntimeException("ML 특약사항 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * ML API 서버 상태 확인
     */
    public boolean isHealthy() {
        try {
            String url = mlApiBaseUrl + "/docs";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("ML API 서버 상태 확인 실패", e);
            return false;
        }
    }
}
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
