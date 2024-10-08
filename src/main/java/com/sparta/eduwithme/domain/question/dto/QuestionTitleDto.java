package com.sparta.eduwithme.domain.question.dto;

import com.sparta.eduwithme.domain.question.entity.Question;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QuestionTitleDto {
    private final Long questionId;
    private final String title;
    private final String category;
    private final String difficulty;
    private final Long point;
    private final Long orderInRoom;
    private final String formattedCreatedAt;
    private final String formattedUpdatedAt;

    public QuestionTitleDto(Question question) {
        this.questionId = question.getId();
        this.title = question.getTitle();
        this.category = question.getCategory().getCategoryName();
        this.difficulty = question.getDifficulty().getLevel();
        this.point = question.getPoint();
        this.orderInRoom = question.getOrderInRoom();
        this.formattedCreatedAt = question.getFormattedCreatedAt();
        this.formattedUpdatedAt = question.getFormattedUpdatedAt();
    }
}