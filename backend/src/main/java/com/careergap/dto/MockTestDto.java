package com.careergap.dto;

import java.util.ArrayList;
import java.util.List;

public class MockTestDto {

    private Long id;
    private String targetRole;
    private String title;
    private String description;
    private int totalQuestions;
    private int timeLimitMinutes;
    private List<TestQuestionItem> questions = new ArrayList<>();

    public static class TestQuestionItem {
        private int questionId;
        private String skill;
        private String question;
        private List<String> options = new ArrayList<>();
        private String category; // Technical, Architecture, Best Practice

        public TestQuestionItem() {
        }

        public TestQuestionItem(int questionId, String skill, String question, List<String> options, String category) {
            this.questionId = questionId;
            this.skill = skill;
            this.question = question;
            this.options = options != null ? options : new ArrayList<>();
            this.category = category;
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

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

    public MockTestDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(int timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public List<TestQuestionItem> getQuestions() {
        return questions;
    }

    public void setQuestions(List<TestQuestionItem> questions) {
        this.questions = questions;
    }
}

