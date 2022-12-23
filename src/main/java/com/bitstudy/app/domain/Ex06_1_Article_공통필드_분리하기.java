package com.bitstudy.app.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/** Article 과 ArticleComment 에 있는 공통 필드 (메타데이터. ID 제외) 들을 별도로 빼서 관리할 거임.
 * 이유는 앞으로 Article 과 ArticleComment 처럼 fk 같은거로 엮여있는 테이블들 만들경우, 모든 domain 안에 있는 파일들에 많은 중복 코드들이 들어가게 된다. 그래서 별도의 파일에 공통되는 것들을 다 몰아넣고 사용하는거 해보기
 *
 * 참고: 공통필드를 빼는건 팀마다 다르다.
 *      중복코드를 싫어해서 한 파일에 다 몰아두는 사람들이 있고,
 *      (유지보수)
 *
 *      중복코드를 괜찮아 해서 각 파일에 그냥 두는 사람도 있다.
 *      (각 파일에 모든 정보가 다 있다. 변경시 유연하게 코드 작업을 할 수 있다)
 *
 * 추출은 두가지 방법으로 할 수 있다.
 *  1) @Embedded - 공통되는 필드들을 하나의 클래스로 만들어서 @Embedded 있는 곳에서 치환 하는 방식
 *
 *  2) @MappedSuperClass - (요즘 실무에서 사용)
 *              @MappedSuperClass 어노테이션이 붙은곳에서 사용
 *
 * 둘의 차이: 사실은 둘이 비슷하지만 @Embedded 방식을 하게 되면 필드가 하나 추가된다.
 *          영속성 컨텍스트를 통해서 데이터를 넘겨 받아서 어플리케이션으로 열었을때에는 어차피 AuditingField 랑 똑같이 보인다.
 *          (중간에 한단계가 더 있다는 뜻)
 *
 *          @MappedSuperClass 는 표준 JPA 에서 제공해주는 클래스. 중간단계 따로 없이 바로 동작
 * */

/** 할일: Lombok 사용하기
 * 주의: maven 때랑 같은 방식인 것들도 이름이 다르게 되어 있으니 헷갈리지 않게 주의!
 *
 * 순서
 * 1) 롬복을 이용해서 클래스를 엔티티로 변경
 * 2) getter/setter, toString 등의 롬복 어노테이션 사용
 * 3) 동등성, 동일성 비교할 수 있는 코드 넣어볼거임
 *
 */

/* @Table - 엔티티와 매핑할 정보를 지정하고
*           사용법) @Index(name="원하는명칭", columnList = "사용할 테이블명")
*                   name 부분을 생략하면 원래 이름 사용한다는 것.
*  @Index - 데이터베이스 인덱스는 추가, 쓰기 및 저장공간을 희생해서 테이블에 대한 데이터 검색 속도를 향상시키는 데이터 구조
*           @Entity 와 세트로 사용
*  */

//@EntityListeners(AuditingEntityListener.class) /* 이거 없으면 테스트 할때 createdAt 때문에 에러남(Ex04 관련) */
@Table(indexes = {
        @Index(columnList = "title"), // 검색속도 빠르게 해주는 작업
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity /* 1) 롬복을 이용해서 클래스를 엔티티로 변경. @Entity 가 붙은 클래스는 JPA 가 관리하게 된다. 그래서 기본키(PK)가 뭔지 알려줘야 한다. 그게 @Id 에너테이션 이다.*/
@Getter /* 2) getter/setter, toString 등의 롬복 어노테이션 사용 */// 롬복의 @Getter 를 쓰면 알아서 모든 필드의 getter 들이 생성된다.
@ToString
public class Ex06_1_Article_공통필드_분리하기 extends AuditingFields {

    @Id // '전체 필드 중에서 이게 PK다' 라고 말해주는거. @Id 가 없으면 @Entity 에서 에러 난다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 해당 필드가 auto_increment 인 경우 @GeneratedValue 를 써서 자동으로 값이 생성되게 해줘야 한다. 기본키 전략
    private Long id;

    /* @Setter 도 @Getter 처럼 클래스 단위로 걸 수 있는데, 그렇게 하면 모든 필드에 접근이 가능해진다.
    * 그런데 id 같은 경우에는 내가 부여하는게 아니라 JPA 에서 자동으로 부여해주는 번호이다. 메타데이터들도 자동으로 JPA 가 세팅하게 만들어야 한다. 그래서 id와 메타데이터는 @Setter 가 필요 없다. @Setter 의 경우는 지금처럼 필요한 필드에만 주는걸 연습하자. (이건 강사님 스타일, 회사마다 다름. 하지만 정교하게 하려면 이렇게 하자.) */

    /** @Column - 해당 컬럼이 not null 인 경우 @Column(nullable = false) 써준다.
     * 기본값은 true 라서 @Column 을 아예 안쓰면 null 가능.
     * @Column(nullable = false, length="숫자") 숫자 안쓰면 기본값 255 적용된다.
     * */
    @Setter @Column(nullable = false) private String title;   // 제목
    @Setter @Column(nullable = false, length = 10000) private String content; // 본문
    @Setter private String hashtag; // 해시태그

    /* 양방향 바인딩
    *
    * */
    @OrderBy("id") // 양방향 바인딩을 할건데 정렬 기준을 id로 하겠다는 뜻
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    @ToString.Exclude /** 이거 중요. 맨 위에 @ToString 이 있는데 마우스 올려보면 '@ToString includes ~ lazy load 어쩌고' 나온다. 이건 퍼포먼스, 메모리 저하를 일으킬 수 있어서 성능적으로 안좋은 영향을 줄 수 있다. 그래서 해당 필드를 가려주세요 하는거 */
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();
    /* 이건 더 중요: @ToString.Exclude 이걸 안해주면 순환참조 이슈가 생길 수 있다.
                    여기서 ToString 이 id, title, content, hashtag 다 찍고 Set<ArticleComment> 부분을 찍으려고 ArticleComment.java 파일에 가서 거기 있는 @ToString 이 원소들 다 찍으려고 하면서 원소들 중에 private Article article; 을 보는 순간 다시 Article 의 @ToString 이 동작하면서 또 모든 원소들을 찍으려고 하고, 그러다가 다시 Set<ArticleComment> 을 보고 또 ArticleComment 로 가서 ToString 돌리고 ... 이런식으로 동작하면서 메모리가 터질 수 있다. 그래서 Set<ArticleComment>에 @ToString.Exclude 를 달아준다.

                    ArticleComment 에 걸지 않고 Article 에 걸어주는 이유는 댓글이 글을 참조하는건 정상적인 경우인데, 반대로 글이 댓글을 참조하는건 일반적인 경우는 아니기 때문에 Article 에 exclude 를 걸어준다.
    *
    * */

    /* 1) Embedded 방식 */
//    class Tmp {
//        @CreatedDate
//        @Column(nullable = false)
//        private LocalDateTime createdAt; // 생성일시
//
//        @CreatedBy
//        @Column(nullable = false, length = 100)
//        private String createdBy; // 생성자
//
//        @LastModifiedDate
//        @Column(nullable = false)
//        private LocalDateTime modifiedAt; // 수정일시
//
//        @LastModifiedBy
//        @Column(nullable = false, length = 100)
//        private String modifiedBy; // 수정자
//    }
//    @Embedded Tmp tmp;


    /**
    jpa auditing: jpa 에서 자동으로 세팅하게 해줄때 사용하는 기능
                    이거 하려면 config 파일이 별도로 있어야 한다.
                    config 패키지 만들어서 JpaConfig 클래스 만들자
     */

//    // 메타데이터
//    @CreatedDate
//    @Column(nullable = false)
//    private LocalDateTime createdAt; // 생성일시
//
//    @CreatedBy
//    @Column(nullable = false, length = 100)
//    private String createdBy; // 생성자
//    /** 다른 생성일시 같은것들은 알아낼 수 있는데, 최초 생성자는 (현재 코드 상태)인증받고 오지 않았기 때문에 따로 알아낼 수가 없다.
//     * 이때 아까 만든 jpaConfig 파일을 사용한다. */
//
//    @LastModifiedDate
//    @Column(nullable = false)
//    private LocalDateTime modifiedAt; // 수정일시
//
//    @LastModifiedBy
//    @Column(nullable = false, length = 100)
//    private String modifiedBy; // 수정자

    /* 위에 처럼 어노테이션을 붙어주기만 하면 auditing 이 작동한다.
    * @CreatedDate : 최초에 insert 할때 자동으로 한번 넣어준다.
    * @CreatedBy : 최초에 insert 할때 자동으로 한번 넣어준다.
    * @LastModifiedDate : 작성 당시의 시간을 실시간으로 넣어준다.
    * @LastModifiedBy : 작성 당시의 작성자의 이름을 실시간으로 넣어준다.
    * */

    /** Entity 를 만들때는 무조건 기본 생성자가 필요하다.
     * public 또는 protected 만 가능한데, 평생 아무데서도 기본생성자를 안쓰이게 하고 싶어서 protected 로 변경함
     * */
    protected Ex06_1_Article_공통필드_분리하기() { }

    /** 사용자가 입력하는 값만 받기. 나머지는 시스템이 알아서 하게 해주면 됨. */
    private Ex06_1_Article_공통필드_분리하기(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Ex06_1_Article_공통필드_분리하기 of(String title, String content, String hashtag) {
        return new Ex06_1_Article_공통필드_분리하기(title, content, hashtag);
    }
    /* 정적 팩토리 메서드(factory method pattern 중에 하나)
    * 정적 팩토리 메서드란 객체 생성 역할을 하는 클래스 메서드라는 뜻.
    * of 메서드를 이용해서 위에 있는 private 생성자를 직접적으로 사용해서 객체를 생성하게 하는 방법
    *
    * !!! 중요: 무조건 static 으로 놔야 한다. !!!
    *
    * 장점
    *   1) static 이기 때문에 new 를 이요해서 생성자를 만들지 않아도 된다.
    *   2) return 을 가지고 있기 때문에 상속 시 값을 확인할 수 있다. (하위 자료형 객체를 반환할 수 있다.)
    *   3) (중요) 객체 생성을 캡슐화 할 수 있다.
    * */

    /**
     * public : 제한없음.
     * protected : 동일한 패키지, 파생클래스 에서만 접근 가능
     * default : 동일한 패키지 내에서만 접근 가능
     * private : 자기 자신의 클래스 내에서만 접근 가능
     * */


    /* 엄청 어려운 개념!!
    * 만약에 Article 클래스를 이용해서 게시글들을 list 에 담아서 화면을 구성할건데, 그걸 하려면 Collection 을 이용해야 한다.
    *   Collection: 객체의 모음(그룹)
    *               자바가 제공하는 최상위 컬렉션(인터페이스)
    *               하이버네이트는 중복을 허용하고 순서를 보장하지 않는다고 가정
    *   Set: 중복 허용 안함. 순서도 보장하지 않음
    *   List: 중복 허용, 순서 있음
    *   Map: key, value 구조로 되어 있는 특수 컬렉션
    *
    *   list 에 넣거나 또는 list 에 있는 중복 요소를 제거하거나 정렬할 때 비교를 할 수 있어야 하기 때문에
    *   동일성, 동등성 비교를 할 수 있는 equals 랑 hashcode 를 구현해야 한다.
    *
    *   모든 데이터들을 비교해도 되지만, 다 불러와서 비교하면 느려질 수 있다.
    *   사실 id 만 같으면 두 엔티티가 같다는 뜻이니까 id만 가지고 비교하는걸 구현하자
    *
    *   체크박스 여러번 나올건데 id만 다 체크해서 만들면 됨
    *  */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ex06_1_Article_공통필드_분리하기 article = (Ex06_1_Article_공통필드_분리하기) o;
        return id.equals(article.id);
//        return (article.id).equals(id);
//        return id != null && id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /** == 와 equals 차이
     *
     * == : 동일성 비교, 값이랑 주소값까지 비교.
     * equals : 동등성 비교, 값만 본다
     * hashCode : 객체를 식별하는 Integer 값.
     *            객체가 가지고 있는 데이터를 특정 알고리즘을 적용해서 계산된 정수값을 hashCode 라고 함.
     *            사용하는 이유: 객체를 비교할 때 드는 비용이 낮다.
     *
     *  자바에서 2개의 객체를 비교할 때는 equals()를 사용하는데,
     *  여러 객체를 비교할 때는 equals()를 사용하면 Integer 를 비교하는데 많은 시간이 소요된다. 그래서 hashCode 사용
     *
     * */
}
