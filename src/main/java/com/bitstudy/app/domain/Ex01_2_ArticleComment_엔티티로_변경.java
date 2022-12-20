package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@ToString
@Getter
@Entity /* 테이블과의 매핑 한다는 뜻.
            JPA 가 관리한다.
            PK 키 알아볼 수 있게 필드들 중 하나에 @Id 어노테이션 달아줘야 한다.*/
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
public class Ex01_2_ArticleComment_엔티티로_변경 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    private Article article;
//    @Setter @Column(nullable = false) private Long articleId;
    /** 연관관계 매핑.
     * 연관관계 없이 만들면 private Long articleId; 이런식으로(관계형 데이터베이스 스타일) 하면된다.
     * 그런데 우리는 Article과 ArticleComment가 관계를 맺고 있는걸 객체지향적으로 표현하려고 이렇게 쓸거다.
     * 그러기 위해서 필요한 건 단방향 이라는 뜻의 @ManyToOne 에너테이션을 써주고,
     * 이건 필수값이다 라는 뜻으로 (optional = false) 써준다.
     * '댓글 여러개 : 게시글 1개' 이기 때문에 단방향 방식을 썼다.
     * */

    @Setter
    @Column(nullable = false, length = 500)
    private String content; // 본문

    // 메타데이터
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일시

    @CreatedBy
    @Column(nullable = false, length = 100)
    private String createdBy; // 생성자
    /** 다른 생성일시 같은것들은 알아낼 수 있는데, 최초 생성자는 (현재 코드 상태)인증받고 오지 않았기 때문에 따로 알아낼 수가 없다.
     * 이때 아까 만든 jpaConfig 파일을 사용한다. */

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt; // 수정일시

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modifiedBy; // 수정자

}
