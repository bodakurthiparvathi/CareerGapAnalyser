package com.careergap.dto;

public class NextBestActionDto {

    private String title;
    private String description;
    private String actionType; // "ANALYZE_GAP", "START_MODULE", "COMPLETE_PRACTICE", "VERIFY_SKILL", "TAKE_MOCK_TEST", "REVISE_TOPIC"
    private String targetUrl;
    private String priority; // "HIGH", "MEDIUM", "RECOMMENDED"
    private String badge;
    private Long targetId; // e.g. moduleId or testId

    public NextBestActionDto() {
    }

    public NextBestActionDto(String title, String description, String actionType, String targetUrl,
                             String priority, String badge, Long targetId) {
        this.title = title;
        this.description = description;
        this.actionType = actionType;
        this.targetUrl = targetUrl;
        this.priority = priority;
        this.badge = badge;
        this.targetId = targetId;
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

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
}

