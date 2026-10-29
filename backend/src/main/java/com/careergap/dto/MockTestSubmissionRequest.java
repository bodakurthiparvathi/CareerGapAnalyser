package com.careergap.dto;

import java.util.HashMap;
import java.util.Map;

public class MockTestSubmissionRequest {

    // Map of questionId -> selectedOptionIndex
    private Map<Integer, Integer> answers = new HashMap<>();

    public MockTestSubmissionRequest() {
    }

    public MockTestSubmissionRequest(Map<Integer, Integer> answers) {
        this.answers = answers;
    }

    public Map<Integer, Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, Integer> answers) {
        this.answers = answers;
    }
}

