package com.careergap.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "user_skills")
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String skillName;

    // "Beginner", "Intermediate", "Advanced", "Expert"
    @Column(nullable = false)
    private String proficiencyLevel;

    // "Technical", "Soft"
    @Column(nullable = false)
    private String category = "Technical";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public UserSkill() {
    }

    public UserSkill(String skillName, String proficiencyLevel, String category) {
        this.skillName = skillName;
        this.proficiencyLevel = proficiencyLevel;
        this.category = category;
    }

    public UserSkill(String skillName, String proficiencyLevel, String category, User user) {
        this.skillName = skillName;
        this.proficiencyLevel = proficiencyLevel;
        this.category = category;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
