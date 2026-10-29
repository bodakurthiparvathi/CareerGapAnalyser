package com.careergap.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class OnboardingRequest {

    private String fullName;
    private String phoneNumber;
    private String location;

    // Academic & Education Details
    private String degree;
    private String branch;
    private String college;
    private Integer graduationYear;
    private String academicStatus; // "Final Year Student", "Recent Graduate", "Working Professional", "Career Switcher"

    // Career Preferences
    @NotBlank(message = "Target role is required")
    private String targetRole;

    private String preferredIndustry;
    private Integer yearsOfExperience = 0;
    private String bio;

    // Skills
    private List<SkillDto> technicalSkills = new ArrayList<>();
    private List<String> softSkills = new ArrayList<>();

    // Projects, Experiences, Certifications
    private List<ProjectDto> projects = new ArrayList<>();
    private List<ExperienceDto> experiences = new ArrayList<>();
    private List<CertificationDto> certifications = new ArrayList<>();

    public OnboardingRequest() {
    }

    public static class SkillDto {
        private String skillName;
        private String proficiencyLevel; // "Beginner", "Intermediate", "Advanced", "Expert"
        private String category = "Technical";

        public SkillDto() {
        }

        public SkillDto(String skillName, String proficiencyLevel, String category) {
            this.skillName = skillName;
            this.proficiencyLevel = proficiencyLevel;
            this.category = category;
        }

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }

        public String getProficiencyLevel() {
            return proficiencyLevel;
        }

        public void setProficiencyLevel(String proficiencyLevel) {
            this.proficiencyLevel = proficiencyLevel;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

    public static class ProjectDto {
        private String title;
        private String description;
        private String technologies;
        private String githubUrl;
        private String liveUrl;

        public ProjectDto() {
        }

        public ProjectDto(String title, String description, String technologies, String githubUrl, String liveUrl) {
            this.title = title;
            this.description = description;
            this.technologies = technologies;
            this.githubUrl = githubUrl;
            this.liveUrl = liveUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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
    }

    public static class ExperienceDto {
        private String title;
        private String company;
        private String duration;
        private String description;

        public ExperienceDto() {
        }

        public ExperienceDto(String title, String company, String duration, String description) {
            this.title = title;
            this.company = company;
            this.duration = duration;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class CertificationDto {
        private String name;
        private String issuer;
        private Integer issueYear;
        private String credentialUrl;

        public CertificationDto() {
        }

        public CertificationDto(String name, String issuer, Integer issueYear, String credentialUrl) {
            this.name = name;
            this.issuer = issuer;
            this.issueYear = issueYear;
            this.credentialUrl = credentialUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public Integer getIssueYear() {
            return issueYear;
        }

        public void setIssueYear(Integer issueYear) {
            this.issueYear = issueYear;
        }

        public String getCredentialUrl() {
            return credentialUrl;
        }

        public void setCredentialUrl(String credentialUrl) {
            this.credentialUrl = credentialUrl;
        }
    }

    // Getters and Setters
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

    public List<SkillDto> getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(List<SkillDto> technicalSkills) {
        this.technicalSkills = technicalSkills;
    }

    public List<String> getSoftSkills() {
        return softSkills;
    }

    public void setSoftSkills(List<String> softSkills) {
        this.softSkills = softSkills;
    }

    public List<ProjectDto> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectDto> projects) {
        this.projects = projects;
    }

    public List<ExperienceDto> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<ExperienceDto> experiences) {
        this.experiences = experiences;
    }

    public List<CertificationDto> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<CertificationDto> certifications) {
        this.certifications = certifications;
    }
}
