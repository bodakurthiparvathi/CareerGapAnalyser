package com.careergap.dto;

import jakarta.validation.constraints.NotBlank;

public class SkillAnalysisRequest {

    @NotBlank(message = "Target role is required")
    private String targetRole;

    private String jobDescription;

    private String resumeText;

    private Long resumeId;

    public SkillAnalysisRequest() {
    }

    public SkillAnalysisRequest(String targetRole, String jobDescription, String resumeText) {
        this.targetRole = targetRole;
        this.jobDescription = jobDescription;
        this.resumeText = resumeText;
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

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }
}
