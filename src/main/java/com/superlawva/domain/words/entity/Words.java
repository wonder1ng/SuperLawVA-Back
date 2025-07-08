<<<<<<< HEAD
// Words.java
package com.superlawva.domain.words.entity;

=======
package com.superlawva.domain.words.entity;


>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

<<<<<<< HEAD
=======

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
@Entity
@Table(name = "words")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Words {
<<<<<<< HEAD
    
    @Id
    @Column(name = "word", length = 255, nullable = false)
    private String word;  // Primary Key로 사용되는 용어명
    
=======


    @Id
    @Column(name = "word", length = 255, nullable = false)
    private String word;  // Primary Key로 사용되는 용어명


>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;  // 용어 설명
}