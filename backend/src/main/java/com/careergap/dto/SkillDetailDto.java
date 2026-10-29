package com.careergap.dto;

public class SkillDetailDto {

    private String skillName;
    private String currentProficiency; // "None / Unverified", "Beginner", "Intermediate", "Advanced", "Expert"
    private String requiredProficiency; // "Beginner", "Intermediate", "Advanced", "Expert"
    private String classification; // "Strong", "Developing", "Missing", "Optional"
    private String evidence; // Specific quote or context from resume, or "No evidence found"
    private int confidenceScore; // 0 - 100
    private int priorityScore; // 0 - 100
    private String priorityLevel; // "High", "Medium", "Low"
    private String category; // "Technical", "Soft"
    private String importance; // "Essential", "Desirable", "Optional"
    private String estimatedEffort; // "Low (1-2 wks)", "Medium (3-4 wks)", "High (5+ wks)"
    private String explanation; // Why this classification was assigned
    private String recommendedAction; // Actionable learning or project recommendation

    public SkillDetailDto() {
    }

    public SkillDetailDto(String skillName, String currentProficiency, String requiredProficiency,
                          String classification, String evidence, int confidenceScore, int priorityScore,
                          String priorityLevel, String category, String importance, String estimatedEffort,
                          String explanation, String recommendedAction) {
        this.skillName = skillName;
        this.currentProficiency = currentProficiency;
        this.requiredProficiency = requiredProficiency;
        this.classification = classification;
        this.evidence = evidence;
        this.confidenceScore = confidenceScore;
        this.priorityScore = priorityScore;
        this.priorityLevel = priorityLevel;
        this.category = category;
        this.importance = importance;
        this.estimatedEffort = estimatedEffort;
        this.explanation = explanation;
        this.recommendedAction = recommendedAction;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCurrentProficiency() {
        return currentProficiency;
    }

    public void setCurrentProficiency(String currentProficiency) {
        this.currentProficiency = currentProficiency;
    }

    public String getRequiredProficiency() {
        return requiredProficiency;
    }

    public void setRequiredProficiency(String requiredProficiency) {
        this.requiredProficiency = requiredProficiency;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(int confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(int priorityScore) {
        this.priorityScore = priorityScore;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getEstimatedEffort() {
        return estimatedEffort;
    }

    public void setEstimatedEffort(String estimatedEffort) {
        this.estimatedEffort = estimatedEffort;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }
}

