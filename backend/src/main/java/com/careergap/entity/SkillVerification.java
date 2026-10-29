package com.careergap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill_verifications", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "skill_name"})
})
public class SkillVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String skillName;

    private String targetRole;

    @Column(nullable = false)
    private String status = "UNVERIFIED"; // UNVERIFIED, PENDING_REVIEW, VERIFIED, REJECTED

    private String verificationMethod; // ASSESSMENT_SCORE, PROJECT_EVIDENCE, COMPREHENSIVE_AUDIT

    private Integer assessmentScore; // e.g. 85

    private String projectTitle;

    private String projectGithubUrl;

    private String projectLiveUrl;

    @Column(columnDefinition = "TEXT")
    private String projectEvidenceDetails; // How skill was applied and tested

    private String verificationBadge; // "Verified Competency", "Production-Ready"

    @Column(columnDefinition = "TEXT")
    private String evaluatorFeedback; // Detailed evaluation explanation

    private LocalDateTime verifiedAt;

    private LocalDateTime createdAt;

    public SkillVerification() {
    }

    public SkillVerification(User user, String skillName, String targetRole) {
        this.user = user;
        this.skillName = skillName;
        this.targetRole = targetRole;
        this.status = "UNVERIFIED";
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getProjectEvidenceDetails() {
        return projectEvidenceDetails;
    }

    public void setProjectEvidenceDetails(String projectEvidenceDetails) {
        this.projectEvidenceDetails = projectEvidenceDetails;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

