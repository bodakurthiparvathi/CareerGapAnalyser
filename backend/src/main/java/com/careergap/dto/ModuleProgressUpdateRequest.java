package com.careergap.dto;

import jakarta.validation.constraints.NotBlank;

public class ModuleProgressUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED

    private String notes;

    public ModuleProgressUpdateRequest() {
    }

    public ModuleProgressUpdateRequest(String status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

