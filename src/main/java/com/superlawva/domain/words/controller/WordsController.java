<<<<<<< HEAD
// WordsController.java
=======
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
package com.superlawva.domain.words.controller;

import com.superlawva.domain.words.dto.*;
import com.superlawva.domain.words.service.WordsService;
<<<<<<< HEAD
=======
import com.superlawva.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
<<<<<<< HEAD
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}) // 프론트엔드 CORS 설정
public class WordsController {
    
    private final WordsService wordsService;
    
    /**
     * 용어 검색 API
     * GET /api/terms/search?keyword=검색어&page=1&pageSize=10
     */
    @GetMapping("/terms/search")
    public ResponseEntity<WordsSearchResponseDto> searchTerms(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(value = "pageSize", defaultValue = "50") @Min(1) int pageSize) {
        
=======
@RequestMapping("/LegalTerms")  // 기존 엔드포인트로 복원
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
@Tag(name = "📚 Legal Terms", description = "법률 용어 검색 및 관리 API")
public class WordsController {

    private final WordsService wordsService;

    @Operation(
        summary = "🔍 법률 용어 검색", 
        description = """
        ## 📖 API 설명
        법률 용어를 검색하여 관련 정보를 조회합니다. 키워드 기반 검색과 페이징을 지원합니다.
      
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "✅ 검색 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "검색 성공 예시",
                    summary = "법률 용어 검색 성공",
                    value = """
                    {
                      "isSuccess": true,
                      "code": "200",
                      "message": "요청에 성공했습니다.",
                      "result": {
                        "totalResults": 25,
                        "page": 1,
                        "pageSize": 10,
                        "data": [
                          {
                            "word": "보증금",
                            "content": "임대차계약에서 임차인이 임대인에게 제공하는 금전적 담보"
                          }
                        ]
                      }
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/terms/search")  // 기존 엔드포인트로 복원
    public ResponseEntity<ApiResponse<WordsSearchResponseDto>> searchTerms(
            @Parameter(description = "검색 키워드", example = "보증금") @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @Parameter(description = "페이지당 결과 수", example = "10") @RequestParam(value = "pageSize", defaultValue = "50") @Min(1) int pageSize) {

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        // DTO 생성
        WordsSearchRequestDto requestDto = WordsSearchRequestDto.builder()
                .keyword(keyword)
                .page(page)
                .pageSize(Math.min(pageSize, 1000)) // 최대 1000개로 제한
                .build();
<<<<<<< HEAD
        
        // 서비스 호출
        WordsSearchResponseDto responseDto = wordsService.searchWords(requestDto);
        
        return ResponseEntity.ok(responseDto);
    }
    
    /**
     * 인기 검색어 API
     * GET /api/search-keywords/popular
     */
=======

        // 서비스 호출
        WordsSearchResponseDto responseDto = wordsService.searchWords(requestDto);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @Operation(
        summary = "🔥 인기 검색어 조회", 
        description = """
        사용자들이 가장 많이 검색한 법률 용어들을 조회합니다.
   
        """
    )
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
    @GetMapping("/search-keywords/popular")
    public ResponseEntity<PopularKeywordsResponseDto> getPopularKeywords() {
        PopularKeywordsResponseDto responseDto = wordsService.getPopularKeywords();
        return ResponseEntity.ok(responseDto);
    }
<<<<<<< HEAD
    
    /**
     * 용어 등록 API
     * POST /api/upload/words
     */
=======

    @Operation(
        summary = "📝 법률 용어 등록", 
        description = """
        새로운 법률 용어를 시스템에 등록합니다.
       
        **주의사항:**
        - 중복된 용어는 등록할 수 없습니다
        - 용어명은 필수 입력 항목입니다
        - 정의는 100자 이상 권장합니다
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "✅ 용어 등록 성공",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "word": "신규용어",
                        "definition": "새로운 법률 용어의 정의",
                        "category": "계약",
                        "related_terms": ["관련용어1", "관련용어2"],
                        "examples": ["사용 예시 1", "사용 예시 2"],
                        "created_at": "2025-01-15T10:30:00Z"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", 
            description = "❌ 중복된 용어 (이미 존재하는 용어)",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "error": "DUPLICATE_WORD",
                        "message": "이미 존재하는 용어입니다."
                    }
                    """
                )
            )
        )
    })
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
    @PostMapping("/upload/words")
    public ResponseEntity<WordsDto> uploadWords(@Valid @RequestBody WordsUploadRequestDto requestDto) {
        try {
            WordsDto responseDto = wordsService.uploadWord(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException e) {
            log.warn("용어 등록 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }
<<<<<<< HEAD
    
    /**
     * 용어 상세 조회 API
     * GET /api/terms/{word}
     */
    @GetMapping("/terms/{word}")
    public ResponseEntity<WordsDto> getTermDetail(@PathVariable String word) {
=======

    @Operation(
        summary = "📖 용어 상세 조회", 
        description = """
        특정 법률 용어의 상세 정보를 조회합니다.
        
        **사용법:**
        ```javascript
        const word = "보증금";
        const response = await fetch(`/api/terms/${encodeURIComponent(word)}`);
        const result = await response.json();
        console.log('용어 상세:', result);
        ```
        
        **응답 예시:**
        ```json
        {
            "word": "보증금",
            "definition": "임대차계약에서 임차인이 임대인에게 제공하는 금전적 담보",
            "category": "임대차",
            "related_terms": ["임대차", "임차인", "임대인"],
            "examples": [
                "보증금은 계약 종료 시 원상복구 후 반환받을 수 있습니다.",
                "보증금은 월세와 별도로 지급하는 금액입니다."
            ],
            "search_count": 150,
            "created_at": "2024-01-01T00:00:00Z",
            "updated_at": "2025-01-15T10:30:00Z"
        }
        ```
        
        **특징:**
        - 용어명으로 정확히 일치하는 용어만 조회
        - 검색 횟수 정보 포함
        - 관련 용어 및 사용 예시 제공
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "✅ 용어 조회 성공",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "word": "보증금",
                        "definition": "임대차계약에서 임차인이 임대인에게 제공하는 금전적 담보",
                        "category": "임대차",
                        "related_terms": ["임대차", "임차인", "임대인"],
                        "examples": [
                            "보증금은 계약 종료 시 원상복구 후 반환받을 수 있습니다.",
                            "보증금은 월세와 별도로 지급하는 금액입니다."
                        ],
                        "search_count": 150,
                        "created_at": "2024-01-01T00:00:00Z",
                        "updated_at": "2025-01-15T10:30:00Z"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "❌ 용어를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "error": "WORD_NOT_FOUND",
                        "message": "해당 용어를 찾을 수 없습니다."
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/terms/{word}")
    public ResponseEntity<WordsDto> getTermDetail(
            @Parameter(description = "조회할 용어", example = "보증금") @PathVariable String word) {
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        try {
            WordsDto responseDto = wordsService.getWordDetail(word);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            log.warn("용어 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
<<<<<<< HEAD
    
    /**
     * 헬스 체크 API
     * GET /api/health
     */
=======

    @Operation(
        summary = "🔧 데이터베이스 연결 테스트", 
        description = """
        용어 데이터베이스 연결 상태를 테스트합니다.
        
        **사용법:**
        ```javascript
        const response = await fetch('/api/words/test');
        const result = await response.json();
        console.log('DB 연결 상태:', result.database_connected);
        console.log('총 용어 수:', result.total_count);
        ```
        
        **응답 예시:**
        ```json
        {
            "status": "success",
            "total_count": 1250,
            "database_connected": true
        }
        ```
        
        **오류 응답 예시:**
        ```json
        {
            "status": "error",
            "error_message": "데이터베이스 연결 실패",
            "database_connected": false
        }
        ```
        """
    )
    @GetMapping("/words/test")
    public ResponseEntity<?> testDatabaseConnection() {
        try {
            // 간단한 카운트만 확인
            long count = wordsService.getAllWordsCount();

            java.util.Map<String, Object> result = java.util.Map.of(
                    "status", "success",
                    "total_count", count,
                    "database_connected", true
            );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            java.util.Map<String, Object> error = java.util.Map.of(
                    "status", "error",
                    "error_message", e.getMessage(),
                    "database_connected", false
            );

            return ResponseEntity.status(500).body(error);
        }
    }

    @Operation(
        summary = "💚 헬스 체크", 
        description = """
        용어 API 서비스의 상태를 확인합니다.
        
        **사용법:**
        ```javascript
        const response = await fetch('/api/health');
        const status = await response.text();
        console.log('API 상태:', status);
        ```
        
        **응답 예시:**
        ```
        Words API is running!
        ```
        """
    )
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Words API is running!");
    }
}