package com.careergap.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserProfileDto {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String location;

    // Academic & Education Details
    private String degree;
    private String branch;
    private String college;
    private Integer graduationYear;
    private String academicStatus;

    // Career
    private String targetRole;
    private String preferredIndustry;
    private Integer yearsOfExperience;
    private String bio;
    private String education;
    private List<String> softSkills = new ArrayList<>();
    private boolean onboardingCompleted;

    // Collections
    private List<OnboardingRequest.SkillDto> technicalSkills = new ArrayList<>();
    private List<OnboardingRequest.ProjectDto> projects = new ArrayList<>();
    private List<OnboardingRequest.ExperienceDto> experiences = new ArrayList<>();
    private List<OnboardingRequest.CertificationDto> certifications = new ArrayList<>();

    private LocalDateTime createdAt;

    public UserProfileDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getPreferredIndustry() {
        return preferredIndustry;
    }

    public void setPreferredIndustry(String preferredIndustry) {
        this.preferredIndustry = preferredIndustry;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public List<String> getSoftSkills() {
        return softSkills;
    }

    public void setSoftSkills(List<String> softSkills) {
        this.softSkills = softSkills;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public List<OnboardingRequest.SkillDto> getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(List<OnboardingRequest.SkillDto> technicalSkills) {
        this.technicalSkills = technicalSkills;
    }

    public List<OnboardingRequest.ProjectDto> getProjects() {
        return projects;
    }

    public void setProjects(List<OnboardingRequest.ProjectDto> projects) {
        this.projects = projects;
    }

    public List<OnboardingRequest.ExperienceDto> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<OnboardingRequest.ExperienceDto> experiences) {
        this.experiences = experiences;
    }

    public List<OnboardingRequest.CertificationDto> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<OnboardingRequest.CertificationDto> certifications) {
        this.certifications = certifications;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
