package com.careergap.dto;

import java.util.ArrayList;
import java.util.List;

public class LearningDashboardDto {

    private Long roadmapId;
    private String targetRole;
    private String roadmapTitle;
    private int estimatedWeeks;
    private int dailyStudyMinutes = 60;
    private int totalModules;
    private int completedModules;
    private int inProgressModules;
    private int progressPercentage;

    // Streak and time
    private int currentStreakDays;
    private int longestStreakDays;
    private int totalPracticeCount;
    private int totalMockTestsCount;
    private double totalHoursLearned;

    // Verification stats
    private int verifiedSkillsCount;
    private List<SkillVerificationResponse> verifiedSkills = new ArrayList<>();

    // Next best action
    private NextBestActionDto nextBestAction;

    // Modules in ordered path
    private List<LearningModuleDto> modules = new ArrayList<>();

    // Recent test attempt
    private MockTestResultDto latestTestResult;

    public LearningDashboardDto() {
    }

    public Long getRoadmapId() {
        return roadmapId;
    }

    public void setRoadmapId(Long roadmapId) {
        this.roadmapId = roadmapId;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getRoadmapTitle() {
        return roadmapTitle;
    }

    public void setRoadmapTitle(String roadmapTitle) {
        this.roadmapTitle = roadmapTitle;
    }

    public int getEstimatedWeeks() {
        return estimatedWeeks;
    }

    public void setEstimatedWeeks(int estimatedWeeks) {
        this.estimatedWeeks = estimatedWeeks;
    }

    public int getDailyStudyMinutes() {
        return dailyStudyMinutes;
    }

    public void setDailyStudyMinutes(int dailyStudyMinutes) {
        this.dailyStudyMinutes = dailyStudyMinutes;
    }

    public int getTotalModules() {
        return totalModules;
    }

    public void setTotalModules(int totalModules) {
        this.totalModules = totalModules;
    }

    public int getCompletedModules() {
        return completedModules;
    }

    public void setCompletedModules(int completedModules) {
        this.completedModules = completedModules;
    }

    public int getInProgressModules() {
        return inProgressModules;
    }

    public void setInProgressModules(int inProgressModules) {
        this.inProgressModules = inProgressModules;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public int getCurrentStreakDays() {
        return currentStreakDays;
    }

    public void setCurrentStreakDays(int currentStreakDays) {
        this.currentStreakDays = currentStreakDays;
    }

    public int getLongestStreakDays() {
        return longestStreakDays;
    }

    public void setLongestStreakDays(int longestStreakDays) {
        this.longestStreakDays = longestStreakDays;
    }

    public int getTotalPracticeCount() {
        return totalPracticeCount;
    }

    public void setTotalPracticeCount(int totalPracticeCount) {
        this.totalPracticeCount = totalPracticeCount;
    }

    public int getTotalMockTestsCount() {
        return totalMockTestsCount;
    }

    public void setTotalMockTestsCount(int totalMockTestsCount) {
        this.totalMockTestsCount = totalMockTestsCount;
    }

    public double getTotalHoursLearned() {
        return totalHoursLearned;
    }

    public void setTotalHoursLearned(double totalHoursLearned) {
        this.totalHoursLearned = totalHoursLearned;
    }

    public int getVerifiedSkillsCount() {
        return verifiedSkillsCount;
    }

    public void setVerifiedSkillsCount(int verifiedSkillsCount) {
        this.verifiedSkillsCount = verifiedSkillsCount;
    }

    public List<SkillVerificationResponse> getVerifiedSkills() {
        return verifiedSkills;
    }

    public void setVerifiedSkills(List<SkillVerificationResponse> verifiedSkills) {
        this.verifiedSkills = verifiedSkills;
    }

    public NextBestActionDto getNextBestAction() {
        return nextBestAction;
    }

    public void setNextBestAction(NextBestActionDto nextBestAction) {
        this.nextBestAction = nextBestAction;
    }

    public List<LearningModuleDto> getModules() {
        return modules;
    }

    public void setModules(List<LearningModuleDto> modules) {
        this.modules = modules;
    }

    public MockTestResultDto getLatestTestResult() {
        return latestTestResult;
    }

    public void setLatestTestResult(MockTestResultDto latestTestResult) {
        this.latestTestResult = latestTestResult;
    }
}
