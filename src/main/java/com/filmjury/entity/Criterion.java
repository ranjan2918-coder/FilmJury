package com.filmjury.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Represents a fixed judging criterion used to evaluate entries.
 * Examples: Story, Direction, Acting, Cinematography, Editing.
 * Each criterion has a name, description, and maximum possible score.
 */
@Entity
@Table(name = "criteria")
public class Criterion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Criterion name is required")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Positive(message = "Maximum score must be a positive number")
    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    // Default constructor required by JPA
    public Criterion() {
    }

    public Criterion(String name, String description, Integer maxScore) {
        this.name = name;
        this.description = description;
        this.maxScore = maxScore;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }
}
