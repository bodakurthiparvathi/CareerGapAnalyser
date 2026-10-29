package com.careergap.dto;

public class DashboardStatsDto {
    private long totalAnalyses;
    private int averageReadiness;
    private int skillsIdentified;
    private int skillsGapCount;
    private String topTargetRole;
    private String currentReadinessLevel;

    public DashboardStatsDto() {
    }

    public long getTotalAnalyses() {
        return totalAnalyses;
    }

    public void setTotalAnalyses(long totalAnalyses) {
        this.totalAnalyses = totalAnalyses;
    }

    public int getAverageReadiness() {
        return averageReadiness;
    }

    public void setAverageReadiness(int averageReadiness) {
        this.averageReadiness = averageReadiness;
    }

    public int getSkillsIdentified() {
        return skillsIdentified;
    }

    public void setSkillsIdentified(int skillsIdentified) {
        this.skillsIdentified = skillsIdentified;
    }

    public int getSkillsGapCount() {
        return skillsGapCount;
    }

    public void setSkillsGapCount(int skillsGapCount) {
        this.skillsGapCount = skillsGapCount;
    }

    public String getTopTargetRole() {
        return topTargetRole;
    }

    public void setTopTargetRole(String topTargetRole) {
        this.topTargetRole = topTargetRole;
    }

    public String getCurrentReadinessLevel() {
        return currentReadinessLevel;
    }

    public void setCurrentReadinessLevel(String currentReadinessLevel) {
        this.currentReadinessLevel = currentReadinessLevel;
    }
}
