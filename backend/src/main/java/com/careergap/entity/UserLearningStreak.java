package com.careergap.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_learning_streaks")
public class UserLearningStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private int currentStreakDays = 0;

    private int longestStreakDays = 0;

    private LocalDate lastActivityDate;

    private int totalCompletedModules = 0;

    private int totalPracticeQuestions = 0;

    private int totalMockTests = 0;

    private double totalLearningHours = 0.0;

    public UserLearningStreak() {
    }

    public UserLearningStreak(User user) {
        this.user = user;
        this.currentStreakDays = 0;
        this.longestStreakDays = 0;
        this.lastActivityDate = null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDate getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(LocalDate lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public int getTotalCompletedModules() {
        return totalCompletedModules;
    }

    public void setTotalCompletedModules(int totalCompletedModules) {
        this.totalCompletedModules = totalCompletedModules;
    }

    public int getTotalPracticeQuestions() {
        return totalPracticeQuestions;
    }

    public void setTotalPracticeQuestions(int totalPracticeQuestions) {
        this.totalPracticeQuestions = totalPracticeQuestions;
    }

    public int getTotalMockTests() {
        return totalMockTests;
    }

    public void setTotalMockTests(int totalMockTests) {
        this.totalMockTests = totalMockTests;
    }

    public double getTotalLearningHours() {
        return totalLearningHours;
    }

    public void setTotalLearningHours(double totalLearningHours) {
        this.totalLearningHours = totalLearningHours;
    }
}

