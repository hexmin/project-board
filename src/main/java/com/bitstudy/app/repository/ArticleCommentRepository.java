package com.bitstudy.app.repository;

import com.bitstudy.app.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

/** TDD 를 위해서 임시로 만들어 놓은 저장소 (이거로 DB에 접근할거다)
 *
 *  - TDD 만드는 방법
 *  1) 마우스 우클릭 > Go To > Test (ctrl + shift + T)
 *  2) JUnit5 버전인지 확인
 * */
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
}
