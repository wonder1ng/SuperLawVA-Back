<<<<<<< HEAD
// PopularKeywordsResponseDto.java
package com.superlawva.domain.words.dto;

=======
package com.superlawva.domain.words.dto;

import io.swagger.v3.oas.annotations.media.Schema;
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
<<<<<<< HEAD
public class PopularKeywordsResponseDto {
    
    private List<String> keywords;
}
=======
@Schema(description = "인기 검색어 응답 DTO")
public class PopularKeywordsResponseDto {

    @Schema(description = "인기 검색어 목록", example = "[\"보증금\", \"임대차계약\", \"계약서\"]")
    private List<String> keywords;
}
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
