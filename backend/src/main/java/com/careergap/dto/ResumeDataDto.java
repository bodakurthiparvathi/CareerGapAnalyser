package com.careergap.dto;

import java.util.ArrayList;
import java.util.List;

public class ResumeDataDto {

    // Personal / Contact Details
    private String fullName;
    private String email;
    private String phoneNumber;
    private String location;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String summary;

    // Structured Sections
    private List<ResumeSkillItemDto> skills = new ArrayList<>();
    private List<ResumeExperienceDto> experiences = new ArrayList<>();
    private List<ResumeProjectDto> projects = new ArrayList<>();
    private List<ResumeEducationDto> education = new ArrayList<>();
    private List<ResumeCertificationDto> certifications = new ArrayList<>();

    public ResumeDataDto() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<ResumeSkillItemDto> getSkills() {
        return skills;
    }

    public void setSkills(List<ResumeSkillItemDto> skills) {
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    public List<ResumeExperienceDto> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<ResumeExperienceDto> experiences) {
        this.experiences = experiences != null ? experiences : new ArrayList<>();
    }

    public List<ResumeProjectDto> getProjects() {
        return projects;
    }

    public void setProjects(List<ResumeProjectDto> projects) {
        this.projects = projects != null ? projects : new ArrayList<>();
    }

    public List<ResumeEducationDto> getEducation() {
        return education;
    }

    public void setEducation(List<ResumeEducationDto> education) {
        this.education = education != null ? education : new ArrayList<>();
    }

    public List<ResumeCertificationDto> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<ResumeCertificationDto> certifications) {
        this.certifications = certifications != null ? certifications : new ArrayList<>();
    }
}

