package com.filmjury.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for submitting a scorecard via the API.
 * Decouples the request format from the entity structure.
 */
public class ScoreCardRequest {

    @NotNull(message = "Entry ID is required")
    private Long entryId;

    @NotNull(message = "Judge ID is required")
    private Long judgeId;

    @NotNull(message = "Criterion ID is required")
    private Long criterionId;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be at least 0")
    private Integer score;

    // Default constructor
    public ScoreCardRequest() {
    }

    // Getters and Setters

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Long getJudgeId() {
        return judgeId;
    }

    public void setJudgeId(Long judgeId) {
        this.judgeId = judgeId;
    }

    public Long getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(Long criterionId) {
        this.criterionId = criterionId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
