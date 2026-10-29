package com.careergap.dto;

import jakarta.validation.constraints.NotBlank;

public class MentorChatRequest {

    @NotBlank(message = "Message is required")
    private String message;

    public MentorChatRequest() {
    }

    public MentorChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

