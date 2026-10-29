package com.careergap.dto;

import java.time.LocalDateTime;

public class SkillVerificationResponse {

    private Long id;
    private String skillName;
    private String targetRole;
    private String status; // VERIFIED, PENDING_REVIEW, UNVERIFIED, REJECTED
    private String verificationMethod;
    private Integer assessmentScore;
    private String projectTitle;
    private String projectGithubUrl;
    private String projectLiveUrl;
    private String verificationBadge;
    private String evaluatorFeedback;
    private LocalDateTime verifiedAt;

    public SkillVerificationResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getVerificationBadge() {
        return verificationBadge;
    }

    public void setVerificationBadge(String verificationBadge) {
        this.verificationBadge = verificationBadge;
    }

    public String getEvaluatorFeedback() {
        return evaluatorFeedback;
    }

    public void setEvaluatorFeedback(String evaluatorFeedback) {
        this.evaluatorFeedback = evaluatorFeedback;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}

