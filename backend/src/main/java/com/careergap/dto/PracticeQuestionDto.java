package com.careergap.dto;

import java.util.ArrayList;
import java.util.List;

public class PracticeQuestionDto {
    private int questionIndex;
    private String type; // MULTIPLE_CHOICE, CONCEPTUAL, SHORT_ANSWER, SCENARIO_BASED
    private String question;
    private List<String> options = new ArrayList<>();
    private Integer correctOptionIndex;
    private String sampleAnswer;
    private String explanation;
    private String scenarioContext;

    public PracticeQuestionDto() {
    }

    public PracticeQuestionDto(int questionIndex, String type, String question, List<String> options,
                               Integer correctOptionIndex, String sampleAnswer, String explanation, String scenarioContext) {
        this.questionIndex = questionIndex;
        this.type = type;
        this.question = question;
        this.options = options != null ? options : new ArrayList<>();
        this.correctOptionIndex = correctOptionIndex;
        this.sampleAnswer = sampleAnswer;
        this.explanation = explanation;
        this.scenarioContext = scenarioContext;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Integer getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(Integer correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getSampleAnswer() {
        return sampleAnswer;
    }

    public void setSampleAnswer(String sampleAnswer) {
        this.sampleAnswer = sampleAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getScenarioContext() {
        return scenarioContext;
    }

    public void setScenarioContext(String scenarioContext) {
        this.scenarioContext = scenarioContext;
    }
}

