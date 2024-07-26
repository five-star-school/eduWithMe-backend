package com.sparta.eduwithme.domain.question;

import com.sparta.eduwithme.domain.question.entity.LearningStatus;
import com.sparta.eduwithme.domain.question.entity.Question;
import com.sparta.eduwithme.domain.question.entity.QuestionType;
import com.sparta.eduwithme.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LearningStatusRepository extends JpaRepository<LearningStatus, Long> {
    Page<LearningStatus> findByUserAndQuestionType(User user, QuestionType questionType, Pageable pageable);

    Optional<LearningStatus> findByQuestionAndUser(Question question, User user);

    @Query("SELECT SUM(q.point) FROM LearningStatus ls JOIN ls.question q WHERE ls.user.id = :userId AND ls.questionType = :questionType")
    Long findTotalPointsByUserIdAndQuestionType(@Param("userId") Long userId, @Param("questionType") QuestionType questionType);
}
