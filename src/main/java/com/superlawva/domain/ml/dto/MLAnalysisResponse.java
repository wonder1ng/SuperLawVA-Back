<<<<<<< HEAD
//package com.superlawva.domain.ml.dto;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class MLAnalysisResponse {
//
//    @JsonProperty("success")
//    private boolean success;
//
//    @JsonProperty("message")
//    private String message;
//
//    @JsonProperty("contract_id")
//    private String contractId;
//
//    @JsonProperty("user_id")
//    private String userId;
//
//    @JsonProperty("request_type")
//    private String requestType;
//
//    @JsonProperty("analysis_result")
//    private AnalysisResult analysisResult;
//
//    @JsonProperty("generated_content")
//    private GeneratedContent generatedContent;
//
//    @JsonProperty("processing_info")
//    private ProcessingInfo processingInfo;
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class AnalysisResult {
//        @JsonProperty("risk_score")
//        private Double riskScore; // 0.0 ~ 1.0
//
//        @JsonProperty("risk_level")
//        private String riskLevel; // "LOW", "MEDIUM", "HIGH", "CRITICAL"
//
//        @JsonProperty("risk_factors")
//        private List<String> riskFactors;
//
//        @JsonProperty("legal_issues")
//        private List<LegalIssue> legalIssues;
//
//        @JsonProperty("recommendations")
//        private List<String> recommendations;
//
//        @JsonProperty("compliance_check")
//        private ComplianceCheck complianceCheck;
//
//        @JsonProperty("summary")
//        private String summary;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class LegalIssue {
//        @JsonProperty("category")
//        private String category; // "PAYMENT", "TERMINATION", "LIABILITY", etc.
//
//        @JsonProperty("severity")
//        private String severity; // "LOW", "MEDIUM", "HIGH", "CRITICAL"
//
//        @JsonProperty("description")
//        private String description;
//
//        @JsonProperty("clause_reference")
//        private String clauseReference;
//
//        @JsonProperty("suggestion")
//        private String suggestion;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class ComplianceCheck {
//        @JsonProperty("is_compliant")
//        private boolean isCompliant;
//
//        @JsonProperty("violations")
//        private List<String> violations;
//
//        @JsonProperty("missing_clauses")
//        private List<String> missingClauses;
//
//        @JsonProperty("regulatory_issues")
//        private List<String> regulatoryIssues;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class GeneratedContent {
//        @JsonProperty("content_type")
//        private String contentType; // "PROOF_OF_CONTENT", "LEGAL_ANALYSIS", "CONTRACT_SUMMARY"
//
//        @JsonProperty("title")
//        private String title;
//
//        @JsonProperty("content")
//        private String content;
//
//        @JsonProperty("sections")
//        private List<ContentSection> sections;
//
//        @JsonProperty("templates_used")
//        private List<String> templatesUsed;
//
//        @JsonProperty("confidence_score")
//        private Double confidenceScore; // 0.0 ~ 1.0
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class ContentSection {
//        @JsonProperty("section_name")
//        private String sectionName;
//
//        @JsonProperty("section_content")
//        private String sectionContent;
//
//        @JsonProperty("section_type")
//        private String sectionType; // "HEADER", "BODY", "FOOTER", "SIGNATURE"
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class ProcessingInfo {
//        @JsonProperty("model_name")
//        private String modelName;
//
//        @JsonProperty("model_version")
//        private String modelVersion;
//
//        @JsonProperty("processing_time_seconds")
//        private Double processingTimeSeconds;
//
//        @JsonProperty("token_count")
//        private Integer tokenCount;
//
//        @JsonProperty("quality_score")
//        private Double qualityScore; // 0.0 ~ 1.0
//
//        @JsonProperty("processed_at")
//        private String processedAt;
//
//        @JsonProperty("additional_metadata")
//        private Map<String, Object> additionalMetadata;
//    }
//}
=======
package com.superlawva.domain.ml.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MLAnalysisResponse {

    @JsonProperty("id")
    private Integer id; // ML API에서 생성한 분석 ID

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("contract_id")
    private Integer contractId; // ML API에서 0으로 반환되는 문제 있음

    @JsonProperty("created_date")
    private String createdDate; // ISO 8601 형식

    @JsonProperty("articles")
    private List<ArticleAnalysis> articles;

    @JsonProperty("agreements")
    private List<AgreementAnalysis> agreements;

    @JsonProperty("recommended_agreements")
    private List<RecommendedAgreement> recommendedAgreements;

    @JsonProperty("analysis_metadata")
    private AnalysisMetadata analysisMetadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ArticleAnalysis {
        @JsonProperty("result")
        private Boolean result; // true: 문제없음, false: 문제있음

        @JsonProperty("content")
        private String content; // 원본 조항 내용

        @JsonProperty("reason")
        private String reason; // 분석 이유/설명

        @JsonProperty("suggested_revision")
        private String suggestedRevision; // 수정 제안 (문제 있을 때만)

        @JsonProperty("negotiation_points")
        private String negotiationPoints; // 협상 포인트 (문제 있을 때만)

        @JsonProperty("legal_basis")
        private LegalBasis legalBasis; // 법적 근거

        @JsonProperty("case_basis")
        private List<CaseBasis> caseBasis; // 판례 근거
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgreementAnalysis {
        @JsonProperty("result")
        private Boolean result; // true: 문제없음, false: 문제있음

        @JsonProperty("content")
        private String content; // 원본 특약 내용

        @JsonProperty("reason")
        private String reason; // 분석 이유/설명

        @JsonProperty("suggested_revision")
        private String suggestedRevision; // 수정 제안 (문제 있을 때만)

        @JsonProperty("negotiation_points")
        private String negotiationPoints; // 협상 포인트 (문제 있을 때만)

        @JsonProperty("legal_basis")
        private LegalBasis legalBasis; // 법적 근거

        @JsonProperty("case_basis")
        private List<CaseBasis> caseBasis; // 판례 근거
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LegalBasis {
        @JsonProperty("law_id")
        private Integer lawId;

        @JsonProperty("law")
        private String law; // 법령명

        @JsonProperty("explanation")
        private String explanation; // 법령 설명

        @JsonProperty("content")
        private String content; // 조문 내용
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CaseBasis {
        @JsonProperty("case_id")
        private Integer caseId;

        @JsonProperty("case")
        private String caseName; // 판례명

        @JsonProperty("explanation")
        private String explanation; // 판례 설명

        @JsonProperty("link")
        private String link; // 판례 링크
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalysisMetadata {
        @JsonProperty("model")
        private String model; // "Claude Sonnet 4"

        @JsonProperty("generation_time")
        private Double generationTime; // 처리 시간 (초)

        @JsonProperty("user_agent")
        private String userAgent; // "Mozilla"

        @JsonProperty("version")
        private String version; // "v1.2.3"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecommendedAgreement {
        @JsonProperty("reason")
        private String reason;

        @JsonProperty("suggested_revision")
        private String suggestedRevision;

        @JsonProperty("negotiation_points")
        private String negotiationPoints;

        @JsonProperty("legal_basis")
        private LegalBasis legalBasis;

        @JsonProperty("case_basis")
        private List<CaseBasis> caseBasis;
    }
}
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
