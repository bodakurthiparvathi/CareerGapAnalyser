package com.careergap.dto;

import java.util.ArrayList;
import java.util.List;

public class ResumeTailorResponse {

    private ResumeDataDto tailoredResume;
    private List<String> targetKeywords = new ArrayList<>();
    private List<String> matchedKeywords = new ArrayList<>();
    private List<String> missingKeywords = new ArrayList<>();
    private List<String> keywordRecommendations = new ArrayList<>();
    private List<BulletImprovementDto> bulletImprovements = new ArrayList<>();
    private int matchScore; // 0 - 100
    private String alignmentSummary;

    public ResumeTailorResponse() {
    }

    public ResumeDataDto getTailoredResume() {
        return tailoredResume;
    }

    public void setTailoredResume(ResumeDataDto tailoredResume) {
        this.tailoredResume = tailoredResume;
    }

    public List<String> getTargetKeywords() {
        return targetKeywords;
    }

    public void setTargetKeywords(List<String> targetKeywords) {
        this.targetKeywords = targetKeywords != null ? targetKeywords : new ArrayList<>();
    }

    public List<String> getMatchedKeywords() {
        return matchedKeywords;
    }

    public void setMatchedKeywords(List<String> matchedKeywords) {
        this.matchedKeywords = matchedKeywords != null ? matchedKeywords : new ArrayList<>();
    }

    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    public void setMissingKeywords(List<String> missingKeywords) {
        this.missingKeywords = missingKeywords != null ? missingKeywords : new ArrayList<>();
    }

    public List<String> getKeywordRecommendations() {
        return keywordRecommendations;
    }

    public void setKeywordRecommendations(List<String> keywordRecommendations) {
        this.keywordRecommendations = keywordRecommendations != null ? keywordRecommendations : new ArrayList<>();
    }

    public List<BulletImprovementDto> getBulletImprovements() {
        return bulletImprovements;
    }

    public void setBulletImprovements(List<BulletImprovementDto> bulletImprovements) {
        this.bulletImprovements = bulletImprovements != null ? bulletImprovements : new ArrayList<>();
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public String getAlignmentSummary() {
        return alignmentSummary;
    }

    public void setAlignmentSummary(String alignmentSummary) {
        this.alignmentSummary = alignmentSummary;
    }
}

