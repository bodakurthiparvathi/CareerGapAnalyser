package com.careergap.dto;

import java.util.List;

public class RoadmapDto {
    private Long id;
    private String title;
    private String targetRole;
    private int estimatedWeeks;
    private List<RoadmapStageDto> stages;

    public RoadmapDto() {
    }

    public static class RoadmapStageDto {
        private int stageNumber;
        private String stageName;
        private String duration; // e.g., "Weeks 1-2"
        private String focusArea;
        private List<String> learningGoals;
        private List<String> recommendedResources;
        private List<String> milestoneProjects;

        public RoadmapStageDto() {
        }

        public int getStageNumber() {
            return stageNumber;
        }

        public void setStageNumber(int stageNumber) {
            this.stageNumber = stageNumber;
        }

        public String getStageName() {
            return stageName;
        }

        public void setStageName(String stageName) {
            this.stageName = stageName;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getFocusArea() {
            return focusArea;
        }

        public void setFocusArea(String focusArea) {
            this.focusArea = focusArea;
        }

        public List<String> getLearningGoals() {
            return learningGoals;
        }

        public void setLearningGoals(List<String> learningGoals) {
            this.learningGoals = learningGoals;
        }

        public List<String> getRecommendedResources() {
            return recommendedResources;
        }

        public void setRecommendedResources(List<String> recommendedResources) {
            this.recommendedResources = recommendedResources;
        }

        public List<String> getMilestoneProjects() {
            return milestoneProjects;
        }

        public void setMilestoneProjects(List<String> milestoneProjects) {
            this.milestoneProjects = milestoneProjects;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public int getEstimatedWeeks() {
        return estimatedWeeks;
    }

    public void setEstimatedWeeks(int estimatedWeeks) {
        this.estimatedWeeks = estimatedWeeks;
    }

    public List<RoadmapStageDto> getStages() {
        return stages;
    }

    public void setStages(List<RoadmapStageDto> stages) {
        this.stages = stages;
    }
}
