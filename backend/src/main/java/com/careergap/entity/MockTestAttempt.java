package com.careergap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mock_test_attempts")
public class MockTestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private MockTest mockTest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int score; // 0 - 100 percentage

    private int totalQuestions;

    private int correctCount;

    @Column(columnDefinition = "TEXT")
    private String strengthsJson; // JSON array of mastered topics

    @Column(columnDefinition = "TEXT")
    private String weaknessesJson; // JSON array of topics with errors

    @Column(columnDefinition = "TEXT")
    private String revisionTopicsJson; // JSON array of recommended revision topics

    @Column(columnDefinition = "TEXT")
    private String answersJson; // JSON array of user submitted answers + evaluation feedback

    private LocalDateTime attemptedAt;

    public MockTestAttempt() {
    }

    @PrePersist
    protected void onCreate() {
        this.attemptedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MockTest getMockTest() {
        return mockTest;
    }

    public void setMockTest(MockTest mockTest) {
        this.mockTest = mockTest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public String getStrengthsJson() {
        return strengthsJson;
    }

    public void setStrengthsJson(String strengthsJson) {
        this.strengthsJson = strengthsJson;
    }

    public String getWeaknessesJson() {
        return weaknessesJson;
    }

    public void setWeaknessesJson(String weaknessesJson) {
        this.weaknessesJson = weaknessesJson;
    }

    public String getRevisionTopicsJson() {
        return revisionTopicsJson;
    }

    public void setRevisionTopicsJson(String revisionTopicsJson) {
        this.revisionTopicsJson = revisionTopicsJson;
    }

    public String getAnswersJson() {
        return answersJson;
    }

    public void setAnswersJson(String answersJson) {
        this.answersJson = answersJson;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
}

