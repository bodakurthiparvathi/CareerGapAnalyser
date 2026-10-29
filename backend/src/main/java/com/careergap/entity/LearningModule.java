package com.careergap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_modules")
public class LearningModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private LearningRoadmap roadmap;

    @Column(nullable = false)
    private String skillName;

    private String category; // "Technical", "Soft"

    private int orderIndex; // 1, 2, 3... based on dependency order

    private String estimatedHours; // e.g. "6 hours"

    private String currentProficiency; // e.g. "Beginner / Unverified"

    private String targetProficiency; // e.g. "Advanced / Production-Ready"

    @Column(columnDefinition = "TEXT")
    private String whyItMatters; // Why the skill matters for target job

    @Column(columnDefinition = "TEXT")
    private String learningObjectivesJson; // JSON array of string objectives

    @Column(columnDefinition = "TEXT")
    private String suitableResourcesJson; // JSON array of resource objects

    @Column(columnDefinition = "TEXT")
    private String contentExplanations; // Conceptual guide & code examples

    @Column(columnDefinition = "TEXT")
    private String practiceExercisesJson; // JSON array of exercise prompts

    @Column(columnDefinition = "TEXT")
    private String practiceQuestionsJson; // JSON array of MCQ, Conceptual, Short Answer, Scenario questions

    @Column(columnDefinition = "TEXT")
    private String dependenciesJson; // JSON array of prerequisite skills

    private LocalDateTime createdAt;

    public LearningModule() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LearningRoadmap getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(LearningRoadmap roadmap) {
        this.roadmap = roadmap;
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

    public String getLearningObjectivesJson() {
        return learningObjectivesJson;
    }

    public void setLearningObjectivesJson(String learningObjectivesJson) {
        this.learningObjectivesJson = learningObjectivesJson;
    }

    public String getSuitableResourcesJson() {
        return suitableResourcesJson;
    }

    public void setSuitableResourcesJson(String suitableResourcesJson) {
        this.suitableResourcesJson = suitableResourcesJson;
    }

    public String getContentExplanations() {
        return contentExplanations;
    }

    public void setContentExplanations(String contentExplanations) {
        this.contentExplanations = contentExplanations;
    }

    public String getPracticeExercisesJson() {
        return practiceExercisesJson;
    }

    public void setPracticeExercisesJson(String practiceExercisesJson) {
        this.practiceExercisesJson = practiceExercisesJson;
    }

    public String getPracticeQuestionsJson() {
        return practiceQuestionsJson;
    }

    public void setPracticeQuestionsJson(String practiceQuestionsJson) {
        this.practiceQuestionsJson = practiceQuestionsJson;
    }

    public String getDependenciesJson() {
        return dependenciesJson;
    }

    public void setDependenciesJson(String dependenciesJson) {
        this.dependenciesJson = dependenciesJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

