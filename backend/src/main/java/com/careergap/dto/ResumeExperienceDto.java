package com.careergap.dto;

import java.util.ArrayList;
import java.util.List;

public class ResumeExperienceDto {

    private Long id;
    private String role;
    private String company;
    private String location;
    private String duration;
    private List<String> bullets = new ArrayList<>();

    public ResumeExperienceDto() {
    }

    public ResumeExperienceDto(Long id, String role, String company, String location, String duration, List<String> bullets) {
        this.id = id;
        this.role = role;
        this.company = company;
        this.location = location;
        this.duration = duration;
        this.bullets = bullets != null ? bullets : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<String> getBullets() {
        return bullets;
    }

    public void setBullets(List<String> bullets) {
        this.bullets = bullets;
    }
}

