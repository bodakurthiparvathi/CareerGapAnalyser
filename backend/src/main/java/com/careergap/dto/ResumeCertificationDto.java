package com.careergap.dto;

public class ResumeCertificationDto {

    private Long id;
    private String name;
    private String issuer;
    private String issueYear;
    private String credentialUrl;

    public ResumeCertificationDto() {
    }

    public ResumeCertificationDto(Long id, String name, String issuer, String issueYear, String credentialUrl) {
        this.id = id;
        this.name = name;
        this.issuer = issuer;
        this.issueYear = issueYear;
        this.credentialUrl = credentialUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getIssueYear() {
        return issueYear;
    }

    public void setIssueYear(String issueYear) {
        this.issueYear = issueYear;
    }

    public String getCredentialUrl() {
        return credentialUrl;
    }

    public void setCredentialUrl(String credentialUrl) {
        this.credentialUrl = credentialUrl;
    }
}

