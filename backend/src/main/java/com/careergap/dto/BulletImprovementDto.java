package com.careergap.dto;

public class BulletImprovementDto {

    private String section; // "PROJECTS" or "EXPERIENCE"
    private String itemTitle;
    private String originalBullet;
    private String improvedBullet;
    private String rationale;
    private boolean accepted = false;

    public BulletImprovementDto() {
    }

    public BulletImprovementDto(String section, String itemTitle, String originalBullet, String improvedBullet, String rationale) {
        this.section = section;
        this.itemTitle = itemTitle;
        this.originalBullet = originalBullet;
        this.improvedBullet = improvedBullet;
        this.rationale = rationale;
        this.accepted = false;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getOriginalBullet() {
        return originalBullet;
    }

    public void setOriginalBullet(String originalBullet) {
        this.originalBullet = originalBullet;
    }

    public String getImprovedBullet() {
        return improvedBullet;
    }

    public void setImprovedBullet(String improvedBullet) {
        this.improvedBullet = improvedBullet;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}

