package com.careergap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PracticeSubmissionRequest {

    @NotNull(message = "Question index is required")
    private Integer questionIndex;

    @NotBlank(message = "Question type is required")
    private String questionType;

    @NotBlank(message = "User answer is required")
    private String userAnswer;

    public PracticeSubmissionRequest() {
    }

    public PracticeSubmissionRequest(Integer questionIndex, String questionType, String userAnswer) {
        this.questionIndex = questionIndex;
        this.questionType = questionType;
        this.userAnswer = userAnswer;
    }

    public Integer getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(Integer questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}

