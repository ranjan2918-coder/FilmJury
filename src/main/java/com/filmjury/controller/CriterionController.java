package com.filmjury.controller;

import com.filmjury.entity.Criterion;
import com.filmjury.service.CriterionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Criterion operations.
 * Provides CRUD endpoints for managing judging criteria.
 */
@RestController
@RequestMapping("/api/criteria")
public class CriterionController {

    private final CriterionService criterionService;

    public CriterionController(CriterionService criterionService) {
        this.criterionService = criterionService;
    }

    /**
     * POST /api/criteria — Create a new criterion.
     */
    @PostMapping
    public ResponseEntity<Criterion> createCriterion(@Valid @RequestBody Criterion criterion) {
        Criterion created = criterionService.createCriterion(criterion);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * GET /api/criteria — Retrieve all criteria.
     */
    @GetMapping
    public ResponseEntity<List<Criterion>> getAllCriteria() {
        return ResponseEntity.ok(criterionService.getAllCriteria());
    }

    /**
     * GET /api/criteria/{id} — Retrieve a single criterion by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Criterion> getCriterionById(@PathVariable Long id) {
        return ResponseEntity.ok(criterionService.getCriterionById(id));
    }

    /**
     * PUT /api/criteria/{id} — Update an existing criterion.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Criterion> updateCriterion(@PathVariable Long id,
                                                     @Valid @RequestBody Criterion criterion) {
        return ResponseEntity.ok(criterionService.updateCriterion(id, criterion));
    }

    /**
     * DELETE /api/criteria/{id} — Delete a criterion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCriterion(@PathVariable Long id) {
        criterionService.deleteCriterion(id);
        return ResponseEntity.ok(Map.of("message", "Criterion deleted successfully"));
    }
}
