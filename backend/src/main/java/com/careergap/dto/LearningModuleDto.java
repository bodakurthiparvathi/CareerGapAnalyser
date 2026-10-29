package com.careergap.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LearningModuleDto {

    private Long id;
    private Long roadmapId;
    private String skillName;
    private String category;
    private int orderIndex;
    private String estimatedHours;
    private String currentProficiency;
    private String targetProficiency;
    private String whyItMatters;
    private List<String> learningObjectives = new ArrayList<>();
    private List<Map<String, String>> suitableResources = new ArrayList<>();
    private String contentExplanations;
    private List<String> practiceExercises = new ArrayList<>();
    private List<PracticeQuestionDto> practiceQuestions = new ArrayList<>();
    private List<String> dependencies = new ArrayList<>();

    // User progress state
    private String status = "NOT_STARTED"; // NOT_STARTED, IN_PROGRESS, COMPLETED
    private int userScore = 0;
    private String userNotes;
    private boolean isVerified = false;
    private String verificationBadge;

    public LearningModuleDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoadmapId() {
        return roadmapId;
    }

    public void setRoadmapId(Long roadmapId) {
        this.roadmapId = roadmapId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(String estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public String getCurrentProficiency() {
        return currentProficiency;
    }

    public void setCurrentProficiency(String currentProficiency) {
        this.currentProficiency = currentProficiency;
    }

    public String getTargetProficiency() {
        return targetProficiency;
    }

    public void setTargetProficiency(String targetProficiency) {
        this.targetProficiency = targetProficiency;
    }

    public String getWhyItMatters() {
        return whyItMatters;
    }

    public void setWhyItMatters(String whyItMatters) {
        this.whyItMatters = whyItMatters;
    }

    public List<String> getLearningObjectives() {
        return learningObjectives;
    }

    public void setLearningObjectives(List<String> learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    public List<Map<String, String>> getSuitableResources() {
        return suitableResources;
    }

    public void setSuitableResources(List<Map<String, String>> suitableResources) {
        this.suitableResources = suitableResources;
    }

    public String getContentExplanations() {
        return contentExplanations;
    }

    public void setContentExplanations(String contentExplanations) {
        this.contentExplanations = contentExplanations;
    }

    public List<String> getPracticeExercises() {
        return practiceExercises;
    }

    public void setPracticeExercises(List<String> practiceExercises) {
        this.practiceExercises = practiceExercises;
    }

    public List<PracticeQuestionDto> getPracticeQuestions() {
        return practiceQuestions;
    }

    public void setPracticeQuestions(List<PracticeQuestionDto> practiceQuestions) {
        this.practiceQuestions = practiceQuestions;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public String getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(String userNotes) {
        this.userNotes = userNotes;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getVerificationBadge() {
        return verificationBadge;
    }

    public void setVerificationBadge(String verificationBadge) {
        this.verificationBadge = verificationBadge;
    }
}

