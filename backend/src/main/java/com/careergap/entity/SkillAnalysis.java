package com.careergap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill_analyses")
public class SkillAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String targetRole;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String jobDescription;

    private int overallMatchScore;
    private int technicalScore;
    private int softSkillsScore;

    @Column(columnDefinition = "TEXT")
    private String presentSkills;

    @Column(columnDefinition = "TEXT")
    private String missingSkills;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(columnDefinition = "TEXT")
    private String skillsAnalysisJson;

    @Column(columnDefinition = "TEXT")
    private String calculationCriteria;

    @Column(columnDefinition = "TEXT")
    private String highPriorityGapsJson;

    private String readinessLevel; // "Needs Foundation", "Promising Fit", "High Job Readiness", "Ready to Apply"

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public SkillAnalysis() {
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

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public int getOverallMatchScore() {
        return overallMatchScore;
    }

    public void setOverallMatchScore(int overallMatchScore) {
        this.overallMatchScore = overallMatchScore;
    }

    public int getTechnicalScore() {
        return technicalScore;
    }

    public void setTechnicalScore(int technicalScore) {
        this.technicalScore = technicalScore;
    }

    public int getSoftSkillsScore() {
        return softSkillsScore;
    }

    public void setSoftSkillsScore(int softSkillsScore) {
        this.softSkillsScore = softSkillsScore;
    }

    public String getPresentSkills() {
        return presentSkills;
    }

    public void setPresentSkills(String presentSkills) {
        this.presentSkills = presentSkills;
    }

    public String getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(String missingSkills) {
        this.missingSkills = missingSkills;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getSkillsAnalysisJson() {
        return skillsAnalysisJson;
    }

    public void setSkillsAnalysisJson(String skillsAnalysisJson) {
        this.skillsAnalysisJson = skillsAnalysisJson;
    }

    public String getCalculationCriteria() {
        return calculationCriteria;
    }

    public void setCalculationCriteria(String calculationCriteria) {
        this.calculationCriteria = calculationCriteria;
    }

    public String getHighPriorityGapsJson() {
        return highPriorityGapsJson;
    }

    public void setHighPriorityGapsJson(String highPriorityGapsJson) {
        this.highPriorityGapsJson = highPriorityGapsJson;
    }

    public String getReadinessLevel() {
        return readinessLevel;
    }

    public void setReadinessLevel(String readinessLevel) {
        this.readinessLevel = readinessLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
