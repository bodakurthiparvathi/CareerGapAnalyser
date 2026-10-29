package com.careergap.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ResumeVersionDto {

    private Long id;
    private int versionNumber;
    private String title;
    private String targetJobTitle;
    private String targetJobDescription;
    private String changeSummary;
    private List<String> tailoredKeywords;
    private ResumeDataDto resumeData;
    private LocalDateTime createdAt;

    public ResumeVersionDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
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

    public List<String> getTailoredKeywords() {
        return tailoredKeywords;
    }

    public void setTailoredKeywords(List<String> tailoredKeywords) {
        this.tailoredKeywords = tailoredKeywords;
    }

    public ResumeDataDto getResumeData() {
        return resumeData;
    }

    public void setResumeData(ResumeDataDto resumeData) {
        this.resumeData = resumeData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

