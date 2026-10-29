package com.careergap.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SkillAnalysisResponse {
    private Long id;
    private String targetRole;
    private int overallMatchScore;
    private int technicalScore;
    private int softSkillsScore;
    private String readinessLevel;

    // Detailed structured skills
    private List<SkillDetailDto> skills = new ArrayList<>();
    private List<SkillDetailDto> strongSkills = new ArrayList<>();
    private List<SkillDetailDto> developingSkills = new ArrayList<>();
    private List<SkillDetailDto> missingSkills = new ArrayList<>();
    private List<SkillDetailDto> optionalSkills = new ArrayList<>();
    private List<SkillDetailDto> highPriorityGaps = new ArrayList<>();

    // Legacy string lists for backward compatibility
    private List<String> presentSkills = new ArrayList<>();
    private List<String> missingSkillsList = new ArrayList<>();

    // Strengths and insights
    private List<String> strengths = new ArrayList<>();
    private List<String> weaknesses = new ArrayList<>();
    private List<String> recommendations = new ArrayList<>();

    // Transparency & Criteria
    private String scoreDisclaimer = "This readiness score reflects alignment with the provided job description based on demonstrated resume evidence. It does not guarantee employment or hiring outcomes.";
    private String calculationCriteria;

    // Counts
    private int strongCount;
    private int developingCount;
    private int missingCount;
    private int optionalCount;

    private RoadmapDto roadmap;
    private LocalDateTime createdAt;

    public SkillAnalysisResponse() {
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

    public String getReadinessLevel() {
        return readinessLevel;
    }

    public void setReadinessLevel(String readinessLevel) {
        this.readinessLevel = readinessLevel;
    }

    public List<SkillDetailDto> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDetailDto> skills) {
        this.skills = skills;
    }

    public List<SkillDetailDto> getStrongSkills() {
        return strongSkills;
    }

    public void setStrongSkills(List<SkillDetailDto> strongSkills) {
        this.strongSkills = strongSkills;
    }

    public List<SkillDetailDto> getDevelopingSkills() {
        return developingSkills;
    }

    public void setDevelopingSkills(List<SkillDetailDto> developingSkills) {
        this.developingSkills = developingSkills;
    }

    public List<SkillDetailDto> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<SkillDetailDto> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<SkillDetailDto> getOptionalSkills() {
        return optionalSkills;
    }

    public void setOptionalSkills(List<SkillDetailDto> optionalSkills) {
        this.optionalSkills = optionalSkills;
    }

    public List<SkillDetailDto> getHighPriorityGaps() {
        return highPriorityGaps;
    }

    public void setHighPriorityGaps(List<SkillDetailDto> highPriorityGaps) {
        this.highPriorityGaps = highPriorityGaps;
    }

    public List<String> getPresentSkills() {
        return presentSkills;
    }

    public void setPresentSkills(List<String> presentSkills) {
        this.presentSkills = presentSkills;
    }

    public List<String> getMissingSkillsList() {
        return missingSkillsList;
    }

    public void setMissingSkillsList(List<String> missingSkillsList) {
        this.missingSkillsList = missingSkillsList;
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

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public String getScoreDisclaimer() {
        return scoreDisclaimer;
    }

    public void setScoreDisclaimer(String scoreDisclaimer) {
        this.scoreDisclaimer = scoreDisclaimer;
    }

    public String getCalculationCriteria() {
        return calculationCriteria;
    }

    public void setCalculationCriteria(String calculationCriteria) {
        this.calculationCriteria = calculationCriteria;
    }

    public int getStrongCount() {
        return strongCount;
    }

    public void setStrongCount(int strongCount) {
        this.strongCount = strongCount;
    }

    public int getDevelopingCount() {
        return developingCount;
    }

    public void setDevelopingCount(int developingCount) {
        this.developingCount = developingCount;
    }

    public int getMissingCount() {
        return missingCount;
    }

    public void setMissingCount(int missingCount) {
        this.missingCount = missingCount;
    }

    public int getOptionalCount() {
        return optionalCount;
    }

    public void setOptionalCount(int optionalCount) {
        this.optionalCount = optionalCount;
    }

    public RoadmapDto getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(RoadmapDto roadmap) {
        this.roadmap = roadmap;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
