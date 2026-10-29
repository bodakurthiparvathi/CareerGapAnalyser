package com.careergap.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class StudyPreferenceRequest {

    @Min(value = 15, message = "Daily study time must be at least 15 minutes")
    @Max(value = 480, message = "Daily study time cannot exceed 480 minutes")
    private int dailyStudyMinutes;

    public int getDailyStudyMinutes() {
        return dailyStudyMinutes;
    }

    public void setDailyStudyMinutes(int dailyStudyMinutes) {
        this.dailyStudyMinutes = dailyStudyMinutes;
    }
}
