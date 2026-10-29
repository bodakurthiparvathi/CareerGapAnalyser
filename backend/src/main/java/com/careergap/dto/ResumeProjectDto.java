package com.careergap.dto;

import java.util.ArrayList;
import java.util.List;

public class ResumeProjectDto {

    private Long id;
    private String title;
    private String technologies;
    private String githubUrl;
    private String liveUrl;
    private List<String> bullets = new ArrayList<>();

    public ResumeProjectDto() {
    }

    public ResumeProjectDto(String title, String technologies, String githubUrl, String liveUrl, List<String> bullets) {
        this.title = title;
        this.technologies = technologies;
        this.githubUrl = githubUrl;
        this.liveUrl = liveUrl;
        this.bullets = bullets != null ? bullets : new ArrayList<>();
    }

    public ResumeProjectDto(Long id, String title, String technologies, String githubUrl, String liveUrl, List<String> bullets) {
        this.id = id;
        this.title = title;
        this.technologies = technologies;
        this.githubUrl = githubUrl;
        this.liveUrl = liveUrl;
        this.bullets = bullets != null ? bullets : new ArrayList<>();
    }

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

    public String getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public List<String> getBullets() {
        return bullets;
    }

    public void setBullets(List<String> bullets) {
        this.bullets = bullets != null ? bullets : new ArrayList<>();
    }
}

