package com.careergap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    // Contact & Location
    private String phoneNumber;
    private String location;

    // Academic & Education Details
    private String degree;
    private String branch;
    private String college;
    private Integer graduationYear;
    private String academicStatus; // "Final Year Student", "Recent Graduate", "Working Professional", "Career Switcher"
    private String education;

    // Career Preferences
    private String targetRole;
    private String preferredIndustry;
    private Integer yearsOfExperience;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String softSkills; // Comma-separated or tag list

    private boolean onboardingCompleted = false;

    // Password Reset
    private String resetPasswordToken;
    private LocalDateTime resetPasswordExpiry;

    // Relational Collections
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserSkill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserProject> projects = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserExperience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserCertification> certifications = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(String email, String password, String fullName, String targetRole) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.targetRole = targetRole;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helpers to manage bidirectional relationships cleanly
    public void addSkill(UserSkill skill) {
        skills.add(skill);
        skill.setUser(this);
    }

    public void removeSkill(UserSkill skill) {
        skills.remove(skill);
        skill.setUser(null);
    }

    public void addProject(UserProject project) {
        projects.add(project);
        project.setUser(this);
    }

    public void removeProject(UserProject project) {
        projects.remove(project);
        project.setUser(null);
    }

    public void addExperience(UserExperience exp) {
        experiences.add(exp);
        exp.setUser(this);
    }

    public void removeExperience(UserExperience exp) {
        experiences.remove(exp);
        exp.setUser(null);
    }

    public void addCertification(UserCertification cert) {
        certifications.add(cert);
        cert.setUser(this);
    }

    public void removeCertification(UserCertification cert) {
        certifications.remove(cert);
        cert.setUser(null);
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
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

    public String getSoftSkills() {
        return softSkills;
    }

    public void setSoftSkills(String softSkills) {
        this.softSkills = softSkills;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public LocalDateTime getResetPasswordExpiry() {
        return resetPasswordExpiry;
    }

    public void setResetPasswordExpiry(LocalDateTime resetPasswordExpiry) {
        this.resetPasswordExpiry = resetPasswordExpiry;
    }

    public List<UserSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<UserSkill> skills) {
        this.skills = skills;
    }

    public List<UserProject> getProjects() {
        return projects;
    }

    public void setProjects(List<UserProject> projects) {
        this.projects = projects;
    }

    public List<UserExperience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<UserExperience> experiences) {
        this.experiences = experiences;
    }

    public List<UserCertification> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<UserCertification> certifications) {
        this.certifications = certifications;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
