package com.careergap.dto;

public class ResumeSkillItemDto {

    private String skillName;
    private String category = "Technical"; // Technical, Soft, Tools
    private String proficiencyLevel; // Beginner, Intermediate, Advanced, Expert
    private String skillType = "LEARNED"; // LEARNED, ASSESSED, VERIFIED
    private boolean verified = false;
    private String verificationBadge;

    public ResumeSkillItemDto() {
    }

    public ResumeSkillItemDto(String skillName, String category, String proficiencyLevel) {
        this.skillName = skillName;
        this.category = category;
        this.proficiencyLevel = proficiencyLevel;
    }

    public ResumeSkillItemDto(String skillName, String category, String proficiencyLevel, String skillType) {
        this.skillName = skillName;
        this.category = category;
        this.proficiencyLevel = proficiencyLevel;
        this.skillType = skillType;
    }

    public ResumeSkillItemDto(String skillName, String category, String proficiencyLevel, String skillType, boolean verified, String verificationBadge) {
        this.skillName = skillName;
        this.category = category;
        this.proficiencyLevel = proficiencyLevel;
        this.skillType = skillType;
        this.verified = verified;
        this.verificationBadge = verificationBadge;
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

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerificationBadge() {
        return verificationBadge;
    }

    public void setVerificationBadge(String verificationBadge) {
        this.verificationBadge = verificationBadge;
    }
}

