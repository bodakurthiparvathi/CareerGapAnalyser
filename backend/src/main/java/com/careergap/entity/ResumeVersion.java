package com.careergap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_versions")
public class ResumeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int versionNumber = 1;

    @Column(nullable = false)
    private String title = "Default Resume";

    private String targetJobTitle;

    @Column(columnDefinition = "TEXT")
    private String targetJobDescription;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String resumeDataJson;

    @Column(columnDefinition = "TEXT")
    private String tailoredKeywordsJson;

    @Column(columnDefinition = "TEXT")
    private String changeSummary;

    private LocalDateTime createdAt;

    public ResumeVersion() {
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

    public String getResumeDataJson() {
        return resumeDataJson;
    }

    public void setResumeDataJson(String resumeDataJson) {
        this.resumeDataJson = resumeDataJson;
    }

    public String getTailoredKeywordsJson() {
        return tailoredKeywordsJson;
    }

    public void setTailoredKeywordsJson(String tailoredKeywordsJson) {
        this.tailoredKeywordsJson = tailoredKeywordsJson;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

