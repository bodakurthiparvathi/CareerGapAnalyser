package com.careergap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SaveResumeVersionRequest {

    @NotBlank(message = "Resume title is required")
    private String title = "My Resume";

    private String targetJobTitle;
    private String targetJobDescription;
    private String changeSummary;

    @NotNull(message = "Resume data is required")
    private ResumeDataDto resumeData;

    public SaveResumeVersionRequest() {
    }

    public SaveResumeVersionRequest(String title, String targetJobTitle, String targetJobDescription, String changeSummary, ResumeDataDto resumeData) {
        this.title = title;
        this.targetJobTitle = targetJobTitle;
        this.targetJobDescription = targetJobDescription;
        this.changeSummary = changeSummary;
        this.resumeData = resumeData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }

    public ResumeDataDto getResumeData() {
        return resumeData;
    }

    public void setResumeData(ResumeDataDto resumeData) {
        this.resumeData = resumeData;
    }
}

