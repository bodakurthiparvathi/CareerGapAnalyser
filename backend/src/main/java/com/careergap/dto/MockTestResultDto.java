package com.careergap.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockTestResultDto {

    private Long attemptId;
    private Long testId;
    private String testTitle;
    private String targetRole;
    private int score; // 0 - 100 percentage
    private int totalQuestions;
    private int correctCount;
    private boolean passed; // score >= 70

    private List<String> strengths = new ArrayList<>();
    private List<String> weaknesses = new ArrayList<>();
    private List<String> revisionTopics = new ArrayList<>();
    private List<QuestionReviewItem> questionReviews = new ArrayList<>();
    private LocalDateTime attemptedAt;

    public static class QuestionReviewItem {
        private int questionId;
        private String skill;
        private String question;
        private List<String> options = new ArrayList<>();
        private Integer selectedOptionIndex;
        private Integer correctOptionIndex;
        private boolean isCorrect;
        private String explanation;

        public QuestionReviewItem() {
        }

        public QuestionReviewItem(int questionId, String skill, String question, List<String> options,
                                  Integer selectedOptionIndex, Integer correctOptionIndex, boolean isCorrect, String explanation) {
            this.questionId = questionId;
            this.skill = skill;
            this.question = question;
            this.options = options != null ? options : new ArrayList<>();
            this.selectedOptionIndex = selectedOptionIndex;
            this.correctOptionIndex = correctOptionIndex;
            this.isCorrect = isCorrect;
            this.explanation = explanation;
        }

        public int getQuestionId() {
            return questionId;
        }

        public void setQuestionId(int questionId) {
            this.questionId = questionId;
        }

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public Integer getSelectedOptionIndex() {
            return selectedOptionIndex;
        }

        public void setSelectedOptionIndex(Integer selectedOptionIndex) {
            this.selectedOptionIndex = selectedOptionIndex;
        }

        public Integer getCorrectOptionIndex() {
            return correctOptionIndex;
        }

        public void setCorrectOptionIndex(Integer correctOptionIndex) {
            this.correctOptionIndex = correctOptionIndex;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public void setCorrect(boolean correct) {
            isCorrect = correct;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }

    public MockTestResultDto() {
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestTitle() {
        return testTitle;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle = testTitle;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public List<String> getRevisionTopics() {
        return revisionTopics;
    }

    public void setRevisionTopics(List<String> revisionTopics) {
        this.revisionTopics = revisionTopics;
    }

    public List<QuestionReviewItem> getQuestionReviews() {
        return questionReviews;
    }

    public void setQuestionReviews(List<QuestionReviewItem> questionReviews) {
        this.questionReviews = questionReviews;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
}

