package com.careergap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_roadmaps")
public class LearningRoadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String targetRole;

    private int estimatedWeeks;

    @Column(nullable = false, columnDefinition = "integer default 60")
    private int dailyStudyMinutes = 60;

    @Column(columnDefinition = "TEXT")
    private String stagesJson;

    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private java.util.List<LearningModule> modules = new java.util.ArrayList<>();

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id")
    private SkillAnalysis skillAnalysis;

    public LearningRoadmap() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public int getEstimatedWeeks() {
        return estimatedWeeks;
    }

    public void setEstimatedWeeks(int estimatedWeeks) {
        this.estimatedWeeks = estimatedWeeks;
    }

    public int getDailyStudyMinutes() {
        return dailyStudyMinutes;
    }

    public void setDailyStudyMinutes(int dailyStudyMinutes) {
        this.dailyStudyMinutes = dailyStudyMinutes;
    }

    public String getStagesJson() {
        return stagesJson;
    }

    public void setStagesJson(String stagesJson) {
        this.stagesJson = stagesJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SkillAnalysis getSkillAnalysis() {
        return skillAnalysis;
    }

    public void setSkillAnalysis(SkillAnalysis skillAnalysis) {
        this.skillAnalysis = skillAnalysis;
    }

    public java.util.List<LearningModule> getModules() {
        return modules;
    }

    public void setModules(java.util.List<LearningModule> modules) {
        this.modules = modules;
    }
}
