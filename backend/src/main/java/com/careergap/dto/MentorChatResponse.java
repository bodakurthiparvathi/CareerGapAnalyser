package com.careergap.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MentorChatResponse {

    private String response;
    private String sender = "MENTOR";
    private List<String> suggestedNextActions = new ArrayList<>();
    private LocalDateTime timestamp;

    public MentorChatResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public MentorChatResponse(String response, List<String> suggestedNextActions) {
        this.response = response;
        this.suggestedNextActions = suggestedNextActions != null ? suggestedNextActions : new ArrayList<>();
        this.timestamp = LocalDateTime.now();
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<String> getSuggestedNextActions() {
        return suggestedNextActions;
    }

    public void setSuggestedNextActions(List<String> suggestedNextActions) {
        this.suggestedNextActions = suggestedNextActions;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

