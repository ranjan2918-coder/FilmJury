package com.filmjury.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a judge's score for a specific entry under a specific criterion.
 * Links Judge + Entry + Criterion together with a numeric score.
 *
 * Business Rules enforced in the service layer:
 * 1. A judge can submit only ONE scorecard per entry per criterion.
 * 2. The score must not exceed the criterion's maxScore.
 * 3. The score must be >= 0.
 */
@Entity
@Table(name = "scorecards",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"judge_id", "entry_id", "criterion_id"},
           name = "uk_judge_entry_criterion"
       ))
public class ScoreCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be at least 0")
    @Column(nullable = false)
    private Integer score;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entry_id", nullable = false)
    private Entry entry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "judge_id", nullable = false)
    private Judge judge;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criterion_id", nullable = false)
    private Criterion criterion;

    // Default constructor required by JPA
    public ScoreCard() {
    }

    public ScoreCard(Integer score, Entry entry, Judge judge, Criterion criterion) {
        this.score = score;
        this.entry = entry;
        this.judge = judge;
        this.criterion = criterion;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public Judge getJudge() {
        return judge;
    }

    public void setJudge(Judge judge) {
        this.judge = judge;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }
}
