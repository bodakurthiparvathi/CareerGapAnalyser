package com.careergap.dto;

public class PracticeEvaluationResult {

    private int questionIndex;
    private String questionType;
    private boolean isCorrect;
    private int score; // 0 - 100
    private String feedback; // Evaluation feedback and explanation of mistakes
    private String correctAnswerExplanation;
    private String updatedModuleStatus;
    private int streakDays;

    public PracticeEvaluationResult() {
    }

    public PracticeEvaluationResult(int questionIndex, String questionType, boolean isCorrect, int score,
                                    String feedback, String correctAnswerExplanation, String updatedModuleStatus, int streakDays) {
        this.questionIndex = questionIndex;
        this.questionType = questionType;
        this.isCorrect = isCorrect;
        this.score = score;
        this.feedback = feedback;
        this.correctAnswerExplanation = correctAnswerExplanation;
        this.updatedModuleStatus = updatedModuleStatus;
        this.streakDays = streakDays;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCorrectAnswerExplanation() {
        return correctAnswerExplanation;
    }

    public void setCorrectAnswerExplanation(String correctAnswerExplanation) {
        this.correctAnswerExplanation = correctAnswerExplanation;
    }

    public String getUpdatedModuleStatus() {
        return updatedModuleStatus;
    }

    public void setUpdatedModuleStatus(String updatedModuleStatus) {
        this.updatedModuleStatus = updatedModuleStatus;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }
}

