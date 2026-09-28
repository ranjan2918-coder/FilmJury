package com.filmjury.controller;

import com.filmjury.dto.ScoreCardRequest;
import com.filmjury.entity.ScoreCard;
import com.filmjury.service.ScoreCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for ScoreCard operations.
 * Handles scorecard submissions and retrieval.
 *
 * All business logic (duplicate check, score validation, etc.)
 * is handled in ScoreCardService — NOT here in the controller.
 */
@RestController
@RequestMapping("/api/scorecards")
public class ScoreCardController {

    private final ScoreCardService scoreCardService;

    public ScoreCardController(ScoreCardService scoreCardService) {
        this.scoreCardService = scoreCardService;
    }

    /**
     * POST /api/scorecards — Submit a new scorecard.
     *
     * Business rules enforced by the service:
     * 1. Entry, Judge, and Criterion must exist.
     * 2. Score must be between 0 and criterion's maxScore.
     * 3. Judge must not have already submitted a scorecard for this entry.
     */
    @PostMapping
    public ResponseEntity<ScoreCard> submitScoreCard(@Valid @RequestBody ScoreCardRequest request) {
        ScoreCard scoreCard = scoreCardService.submitScoreCard(
                request.getEntryId(),
                request.getJudgeId(),
                request.getCriterionId(),
                request.getScore()
        );
        return new ResponseEntity<>(scoreCard, HttpStatus.CREATED);
    }

    /**
     * GET /api/scorecards — Retrieve all scorecards.
     */
    @GetMapping
    public ResponseEntity<List<ScoreCard>> getAllScoreCards() {
        return ResponseEntity.ok(scoreCardService.getAllScoreCards());
    }

    /**
     * GET /api/scorecards/{id} — Retrieve a single scorecard by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScoreCard> getScoreCardById(@PathVariable Long id) {
        return ResponseEntity.ok(scoreCardService.getScoreCardById(id));
    }

    /**
     * GET /api/scorecards/entry/{entryId} — Retrieve all scorecards for an entry.
     */
    @GetMapping("/entry/{entryId}")
    public ResponseEntity<List<ScoreCard>> getScoreCardsByEntry(@PathVariable Long entryId) {
        return ResponseEntity.ok(scoreCardService.getScoreCardsByEntryId(entryId));
    }
}
