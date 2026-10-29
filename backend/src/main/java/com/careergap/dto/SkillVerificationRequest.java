package com.careergap.dto;

import jakarta.validation.constraints.NotBlank;

public class SkillVerificationRequest {

    @NotBlank(message = "Skill name is required")
    private String skillName;

    private String targetRole;

    @NotBlank(message = "Verification method is required (ASSESSMENT_SCORE or PROJECT_EVIDENCE)")
    private String verificationMethod; // ASSESSMENT_SCORE, PROJECT_EVIDENCE

    private Integer assessmentScore;

    private String projectTitle;

    private String projectGithubUrl;

    private String projectLiveUrl;

    private String projectEvidenceDetails;

    public SkillVerificationRequest() {
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }

    public Integer getAssessmentScore() {
        return assessmentScore;
    }

    public void setAssessmentScore(Integer assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectGithubUrl() {
        return projectGithubUrl;
    }

    public void setProjectGithubUrl(String projectGithubUrl) {
        this.projectGithubUrl = projectGithubUrl;
    }

    public String getProjectLiveUrl() {
        return projectLiveUrl;
    }

    public void setProjectLiveUrl(String projectLiveUrl) {
        this.projectLiveUrl = projectLiveUrl;
    }

    public String getProjectEvidenceDetails() {
        return projectEvidenceDetails;
    }

    public void setProjectEvidenceDetails(String projectEvidenceDetails) {
        this.projectEvidenceDetails = projectEvidenceDetails;
    }
}

