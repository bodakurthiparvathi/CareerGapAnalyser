package com.careergap.dto;

public class ResumeEducationDto {

    private Long id;
    private String degree;
    private String branch;
    private String institution;
    private String graduationYear;
    private String gradeGpa;

    public ResumeEducationDto() {
    }

    public ResumeEducationDto(Long id, String degree, String branch, String institution, String graduationYear, String gradeGpa) {
        this.id = id;
        this.degree = degree;
        this.branch = branch;
        this.institution = institution;
        this.graduationYear = graduationYear;
        this.gradeGpa = gradeGpa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getGradeGpa() {
        return gradeGpa;
    }

    public void setGradeGpa(String gradeGpa) {
        this.gradeGpa = gradeGpa;
    }
}

