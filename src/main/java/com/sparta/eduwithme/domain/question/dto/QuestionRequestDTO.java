package com.sparta.eduwithme.domain.question.dto;

import com.sparta.eduwithme.domain.question.entity.Category;
import com.sparta.eduwithme.domain.question.entity.Difficulty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class QuestionRequestDTO {
    private String title;
    private String content;
    private Category category;
    private Difficulty difficulty;
    private Long point;
    private List<AnswerRequestDTO> answerList;

    public QuestionRequestDTO(String title, String content, Category category, Difficulty difficulty, Long point, List<AnswerRequestDTO> answerList) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.difficulty = difficulty;
        this.point = point;
        this.answerList = answerList;
    }
}
