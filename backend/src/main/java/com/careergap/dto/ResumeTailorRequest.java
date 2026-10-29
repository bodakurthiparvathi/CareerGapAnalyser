package com.careergap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResumeTailorRequest {

    private String targetJobTitle;

    @NotBlank(message = "Job description cannot be blank")
    private String targetJobDescription;

    @NotNull(message = "Resume data cannot be null")
    private ResumeDataDto resumeData;

    public ResumeTailorRequest() {
    }

    public ResumeTailorRequest(String targetJobTitle, String targetJobDescription, ResumeDataDto resumeData) {
        this.targetJobTitle = targetJobTitle;
        this.targetJobDescription = targetJobDescription;
        this.resumeData = resumeData;
    }

    public String getTargetJobTitle() {
        return targetJobTitle;
    }

    public void setTargetJobTitle(String targetJobTitle) {
        this.targetJobTitle = targetJobTitle;
    }

    public String getTargetJobDescription() {
        return targetJobDescription;
    }

    public void setTargetJobDescription(String targetJobDescription) {
        this.targetJobDescription = targetJobDescription;
    }

    public ResumeDataDto getResumeData() {
        return resumeData;
    }

    public void setResumeData(ResumeDataDto resumeData) {
        this.resumeData = resumeData;
    }
}

