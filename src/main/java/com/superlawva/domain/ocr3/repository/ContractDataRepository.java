package com.superlawva.domain.ocr3.repository;

import com.superlawva.domain.ocr3.entity.ContractData;
<<<<<<< HEAD
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

=======
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
import java.util.List;
import java.util.Optional;

@Repository
<<<<<<< HEAD
public interface ContractDataRepository extends MongoRepository<ContractData, String> {
    
    // Find contracts by user ID
    List<ContractData> findByUserId(String userId);
    
    // Find contracts by contract type
    List<ContractData> findByContractType(String contractType);
    
    // Find contracts by lessor name
    List<ContractData> findByLessorName(String lessorName);
    
    // Find contracts by lessee name
    List<ContractData> findByLesseeName(String lesseeName);
    
    // Find by ID and user ID (for security)
    Optional<ContractData> findByIdAndUserId(String id, String userId);
=======
public interface ContractDataRepository extends JpaRepository<ContractData, Long> {

    // Find contracts by user ID
    List<ContractData> findByUserId(String userId);

    // Find contracts by contract type
    List<ContractData> findByContractType(String contractType);

    // Find contracts by lessor name (embedded field 접근)
    @Query("SELECT c FROM ContractData c WHERE c.lessor.name = :lessorName")
    List<ContractData> findByLessorName(@Param("lessorName") String lessorName);

    // Find contracts by lessee name (embedded field 접근)
    @Query("SELECT c FROM ContractData c WHERE c.lessee.name = :lesseeName")
    List<ContractData> findByLesseeName(@Param("lesseeName") String lesseeName);

    // Find by ID and user ID (for security)
    Optional<ContractData> findByIdAndUserId(Long id, String userId);

    // 생성 상태별 조회
    List<ContractData> findByIsGenerated(Boolean isGenerated);

    // 사용자별 최신 순 조회
    List<ContractData> findByUserIdOrderByCreatedDateDesc(String userId);

    // 계약 타입과 사용자별 조회
    List<ContractData> findByContractTypeAndUserId(String contractType, String userId);

    // 신규 추가: 종료일이 특정 날짜 이후인 모든 계약 조회 (String 타입으로 변경)
    List<ContractData> findByDates_EndDateAfter(String date);
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
}