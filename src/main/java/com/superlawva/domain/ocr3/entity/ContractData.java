package com.superlawva.domain.ocr3.entity;

<<<<<<< HEAD
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "contract")
public class ContractData {
    @Id
    private String id;
    
    @Field("user_id")
    private String userId;
    
    @Field("contract_type")
    private String contractType;
    
    private Dates dates;
    private Property property;
    private Payment payment;
    private List<String> articles;
    private List<String> agreements;
    private Party lessor;
    private Party lessee;
    private Broker broker1;
    private Broker broker2;
    
    private boolean generated;
    
    @Field("file_url")
    private String fileUrl;
    
    @Field("created_date")
    private LocalDateTime createdDate;
    
    @Field("modified_date")
    private LocalDateTime modifiedDate;
    
    @Field("contract_metadata")
    private ContractMetadata contractMetadata;
    
    @Data
    public static class Dates {
        @Field("contract_date")
        private String contractDate;
        
        @Field("start_date")
        private String startDate;
        
        @Field("end_date")
        private String endDate;
    }
    
    @Data
    public static class Property {
        private String address;
        
        @Field("detail_address")
        private String detailAddress;
        
        @Field("rent_section")
        private String rentSection;
        
        @Field("rent_area")
        private String rentArea;
        
        private Land land;
        private Building building;
        
        @Data
        public static class Land {
            @Field("land_type")
            private String landType;
            
            @Field("land_right_rate")
            private String landRightRate;
            
            @Field("land_area")
            private Double landArea;
        }
        
        @Data
        public static class Building {
            @Field("building_constructure")
            private String buildingConstructure;
            
            @Field("building_type")
            private String buildingType;
            
            @Field("building_area")
            private String buildingArea;
        }
    }
    
    @Data
    public static class Payment {
        private Long deposit;
        
        @Field("deposit_kr")
        private String depositKr;
        
        @Field("down_payment")
        private Long downPayment;
        
        @Field("down_payment_kr")
        private String downPaymentKr;
        
        @Field("intermediate_payment")
        private Long intermediatePayment;
        
        @Field("intermediate_payment_kr")
        private String intermediatePaymentKr;
        
        @Field("intermediate_payment_date")
        private String intermediatePaymentDate;
        
        @Field("remaining_balance")
        private Long remainingBalance;
        
        @Field("remaining_balance_kr")
        private String remainingBalanceKr;
        
        @Field("remaining_balance_date")
        private String remainingBalanceDate;
        
        @Field("monthly_rent")
        private Long monthlyRent;
        
        @Field("monthly_rent_date")
        private String monthlyRentDate;
        
        @Field("payment_plan")
        private String paymentPlan;
    }
    
    @Data
    public static class Party {
        private String name;
        
        @Field("id_number")
        private String idNumber;
        
        private String address;
        
        @Field("detail_address")
        private String detailAddress;
        
        @Field("phone_number")
        private String phoneNumber;
        
        @Field("mobile_number")
        private String mobileNumber;
        
        private Agent agent;
        
        @Data
        public static class Agent {
            private String name;
        }
    }
    
    @Data
    public static class Broker {
        private String office;
        
        @Field("license_number")
        private String licenseNumber;
        
        private String address;
        private String representative;
        
        @Field("fao_broker")
        private String faoBroker;
    }
    
    @Data
    public static class ContractMetadata {
        private String model;
        
        @Field("generation_time")
        private Double generationTime;
        
        @Field("user_agent")
        private String userAgent;
        
=======
import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.superlawva.global.security.converter.AriaCryptoConverter;
import com.superlawva.global.security.converter.AesCryptoConverter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contract_data")
@Data
public class ContractData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "contract_type")
    private String contractType;

    @Embedded
    private Dates dates;
    
    @Embedded
    private Property property;
    
    @Embedded
    private Payment payment;
    
    @Lob
    @Column(name = "articles_json", columnDefinition = "TEXT")
    private String articlesJson; // List<String> → JSON String (DB 저장용)
    
    @Lob
    @Column(name = "agreements_json", columnDefinition = "TEXT") 
    private String agreementsJson; // List<String> → JSON String (DB 저장용)
    
    // 🟢 AI 응답에서 직접 매핑용 필드 (DB 저장 안함)
    @Transient
    @JsonProperty("articles")
    private List<String> articles;
    
    @Transient
    @JsonProperty("agreements")
    private List<String> agreements;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "lessor_name")),
        @AttributeOverride(name = "idNumber", column = @Column(name = "lessor_id_number")),
        @AttributeOverride(name = "address", column = @Column(name = "lessor_address")),
        @AttributeOverride(name = "detailAddress", column = @Column(name = "lessor_detail_address")),
        @AttributeOverride(name = "phoneNumber", column = @Column(name = "lessor_phone_number")),
        @AttributeOverride(name = "mobileNumber", column = @Column(name = "lessor_mobile_number"))
    })
    private Party lessor;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "lessee_name")),
        @AttributeOverride(name = "idNumber", column = @Column(name = "lessee_id_number")),
        @AttributeOverride(name = "address", column = @Column(name = "lessee_address")),
        @AttributeOverride(name = "detailAddress", column = @Column(name = "lessee_detail_address")),
        @AttributeOverride(name = "phoneNumber", column = @Column(name = "lessee_phone_number")),
        @AttributeOverride(name = "mobileNumber", column = @Column(name = "lessee_mobile_number"))
    })
    private Party lessee;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "office", column = @Column(name = "broker1_office")),
        @AttributeOverride(name = "licenseNumber", column = @Column(name = "broker1_license_number")),
        @AttributeOverride(name = "address", column = @Column(name = "broker1_address")),
        @AttributeOverride(name = "representative", column = @Column(name = "broker1_representative")),
        @AttributeOverride(name = "faoBroker", column = @Column(name = "broker1_fao_broker"))
    })
    private Broker broker1;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "office", column = @Column(name = "broker2_office")),
        @AttributeOverride(name = "licenseNumber", column = @Column(name = "broker2_license_number")),
        @AttributeOverride(name = "address", column = @Column(name = "broker2_address")),
        @AttributeOverride(name = "representative", column = @Column(name = "broker2_representative")),
        @AttributeOverride(name = "faoBroker", column = @Column(name = "broker2_fao_broker"))
    })
    private Broker broker2;

    @Column(name = "is_generated")
    private Boolean isGenerated;
    
    @Lob
    @Column(name = "recommended_agreements_json", columnDefinition = "TEXT")
    private String recommendedAgreementsJson;
    
    @Lob
    @Column(name = "legal_basis_json", columnDefinition = "TEXT")
    private String legalBasisJson;
    
    @Lob
    @Column(name = "case_basis_json", columnDefinition = "TEXT")
    private String caseBasisJson;
    
    @Lob
    @Column(name = "analysis_metadata_json", columnDefinition = "TEXT")
    private String analysisMetadataJson;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Embedded
    private ContractMetadata contractMetadata;

    /* 전체 계약 JSON 원본 */
    @Convert(converter = AesCryptoConverter.class)
    @Lob
    @Column(name = "contract_json", columnDefinition = "LONGTEXT")
    private String contractJson;

    @Embeddable
    @Data
    public static class Dates {
        @Column(name = "contract_date")
        private String contractDate;

        @Column(name = "start_date")
        private String startDate;

        @Column(name = "end_date")
        private String endDate;
    }

    @Embeddable
    @Data
    public static class Property {
        @Convert(converter = AriaCryptoConverter.class)
        @Column(name = "property_address")
        private String address;

        @Convert(converter = AriaCryptoConverter.class)
        @Column(name = "property_detail_address")
        private String detailAddress;

        @Column(name = "rent_section")
        private String rentSection;

        @Column(name = "rent_area")
        private String rentArea;

        @Embedded
        @AttributeOverrides({
            @AttributeOverride(name = "landType", column = @Column(name = "land_type")),
            @AttributeOverride(name = "landRightRate", column = @Column(name = "land_right_rate")),
            @AttributeOverride(name = "landArea", column = @Column(name = "land_area"))
        })
        private Land land;
        
        @Embedded
        @AttributeOverrides({
            @AttributeOverride(name = "buildingConstructure", column = @Column(name = "building_constructure")),
            @AttributeOverride(name = "buildingType", column = @Column(name = "building_type")),
            @AttributeOverride(name = "buildingArea", column = @Column(name = "building_area"))
        })
        private Building building;

        @Embeddable
        @Data
        public static class Land {
            @Column(name = "land_type")
            private String landType;

            @Column(name = "land_right_rate")
            private String landRightRate;

            @Column(name = "land_area")
            private Double landArea;
        }

        @Embeddable
        @Data
        public static class Building {
            @Column(name = "building_constructure")
            private String buildingConstructure;

            @Column(name = "building_type")
            private String buildingType;

            @Column(name = "building_area")
            private String buildingArea;
        }
    }

    @Embeddable
    @Data
    public static class Payment {
        @Column(name = "deposit")
        private Long deposit;

        @Column(name = "deposit_kr")
        private String depositKr;

        @Column(name = "down_payment")
        private Long downPayment;

        @Column(name = "down_payment_kr")
        private String downPaymentKr;

        @Column(name = "intermediate_payment")
        private Long intermediatePayment;

        @Column(name = "intermediate_payment_kr")
        private String intermediatePaymentKr;

        @Column(name = "intermediate_payment_date")
        private String intermediatePaymentDate;

        @Column(name = "remaining_balance")
        private Long remainingBalance;

        @Column(name = "remaining_balance_kr")
        private String remainingBalanceKr;

        @Column(name = "remaining_balance_date")
        private String remainingBalanceDate;

        @Column(name = "monthly_rent")
        private Long monthlyRent;

        @Column(name = "monthly_rent_date")
        private String monthlyRentDate;

        @Column(name = "payment_plan")
        private String paymentPlan;
    }

    @Embeddable
    @Data
    public static class Party {
        private String name;

        @Convert(converter = AriaCryptoConverter.class)
        @Column(name = "id_number")
        private String idNumber;

        @Convert(converter = AriaCryptoConverter.class)
        private String address;

        @Convert(converter = AriaCryptoConverter.class)
        @Column(name = "detail_address")
        private String detailAddress;

        @Convert(converter = AriaCryptoConverter.class)
        private String phoneNumber;

        @Convert(converter = AriaCryptoConverter.class)
        private String mobileNumber;

        @Column(name = "agent_name", insertable = false, updatable = false)
        private String agentName;
    }

    @Embeddable
    @Data
    public static class Broker {
        private String office;

        @Convert(converter = AriaCryptoConverter.class)
        @Column(name = "license_number")
        private String licenseNumber;

        @Convert(converter = AriaCryptoConverter.class)
        private String address;
        private String representative;

        @Column(name = "fao_broker")
        private String faoBroker;
    }

    @Embeddable
    @Data
    public static class ContractMetadata {
        @Column(name = "metadata_model")
        private String model;

        @Column(name = "generation_time")
        private Double generationTime;

        @Column(name = "user_agent")
        private String userAgent;

        @Column(name = "metadata_version")
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        private String version;
    }
}