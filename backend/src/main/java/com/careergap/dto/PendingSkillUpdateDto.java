package com.careergap.dto;

public class PendingSkillUpdateDto {

    private String skillName;
    private String category = "Technical";
    private String skillType; // "LEARNED", "ASSESSED", "VERIFIED"
    private String proficiencyLevel; // "Beginner", "Intermediate", "Advanced"
    private String source; // e.g. "Completed Learning Module", "Practice Assessment (85%)", "Project Verification: E-Commerce Microservices"
    private String suggestedBulletOrEntry;
    private String verificationBadge;
    private boolean alreadyInResume;
    private String recommendationNote;

    public PendingSkillUpdateDto() {
    }

    public PendingSkillUpdateDto(String skillName, String category, String skillType, String proficiencyLevel,
                                 String source, String suggestedBulletOrEntry, String verificationBadge,
                                 boolean alreadyInResume, String recommendationNote) {
        this.skillName = skillName;
        this.category = category;
        this.skillType = skillType;
        this.proficiencyLevel = proficiencyLevel;
        this.source = source;
        this.suggestedBulletOrEntry = suggestedBulletOrEntry;
        this.verificationBadge = verificationBadge;
        this.alreadyInResume = alreadyInResume;
        this.recommendationNote = recommendationNote;
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

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSuggestedBulletOrEntry() {
        return suggestedBulletOrEntry;
    }

    public void setSuggestedBulletOrEntry(String suggestedBulletOrEntry) {
        this.suggestedBulletOrEntry = suggestedBulletOrEntry;
    }

    public String getVerificationBadge() {
        return verificationBadge;
    }

    public void setVerificationBadge(String verificationBadge) {
        this.verificationBadge = verificationBadge;
    }

    public boolean isAlreadyInResume() {
        return alreadyInResume;
    }

    public void setAlreadyInResume(boolean alreadyInResume) {
        this.alreadyInResume = alreadyInResume;
    }

    public String getRecommendationNote() {
        return recommendationNote;
    }

    public void setRecommendationNote(String recommendationNote) {
        this.recommendationNote = recommendationNote;
    }
}

