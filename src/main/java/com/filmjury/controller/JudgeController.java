package com.filmjury.controller;

import com.filmjury.entity.Judge;
import com.filmjury.service.JudgeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Judge operations.
 * Provides CRUD endpoints for managing judges.
 */
@RestController
@RequestMapping("/api/judges")
public class JudgeController {

    private final JudgeService judgeService;

    public JudgeController(JudgeService judgeService) {
        this.judgeService = judgeService;
    }

    /**
     * POST /api/judges — Create a new judge.
     */
    @PostMapping
    public ResponseEntity<Judge> createJudge(@Valid @RequestBody Judge judge) {
        Judge created = judgeService.createJudge(judge);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * GET /api/judges — Retrieve all judges.
     */
    @GetMapping
    public ResponseEntity<List<Judge>> getAllJudges() {
        return ResponseEntity.ok(judgeService.getAllJudges());
    }

    /**
     * GET /api/judges/{id} — Retrieve a single judge by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Judge> getJudgeById(@PathVariable Long id) {
        return ResponseEntity.ok(judgeService.getJudgeById(id));
    }

    /**
     * PUT /api/judges/{id} — Update an existing judge.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Judge> updateJudge(@PathVariable Long id,
                                             @Valid @RequestBody Judge judge) {
        return ResponseEntity.ok(judgeService.updateJudge(id, judge));
    }

    /**
     * DELETE /api/judges/{id} — Delete a judge.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteJudge(@PathVariable Long id) {
        judgeService.deleteJudge(id);
        return ResponseEntity.ok(Map.of("message", "Judge deleted successfully"));
    }
}
