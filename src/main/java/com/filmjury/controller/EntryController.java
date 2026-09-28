package com.filmjury.controller;

import com.filmjury.entity.Entry;
import com.filmjury.service.EntryService;
import com.filmjury.service.ScoreCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Entry (short film) operations.
 * Provides CRUD endpoints + final score + leaderboard.
 */
@RestController
@RequestMapping("/api/entries")
public class EntryController {

    private final EntryService entryService;
    private final ScoreCardService scoreCardService;

    public EntryController(EntryService entryService, ScoreCardService scoreCardService) {
        this.entryService = entryService;
        this.scoreCardService = scoreCardService;
    }

    /**
     * POST /api/entries — Create a new film entry.
     */
    @PostMapping
    public ResponseEntity<Entry> createEntry(@Valid @RequestBody Entry entry) {
        Entry created = entryService.createEntry(entry);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * GET /api/entries — Retrieve all film entries.
     */
    @GetMapping
    public ResponseEntity<List<Entry>> getAllEntries() {
        return ResponseEntity.ok(entryService.getAllEntries());
    }

    /**
     * GET /api/entries/{id} — Retrieve a single entry by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Entry> getEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(entryService.getEntryById(id));
    }

    /**
     * PUT /api/entries/{id} — Update an existing entry.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Entry> updateEntry(@PathVariable Long id,
                                             @Valid @RequestBody Entry entry) {
        return ResponseEntity.ok(entryService.updateEntry(id, entry));
    }

    /**
     * DELETE /api/entries/{id} — Delete an entry.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteEntry(@PathVariable Long id) {
        entryService.deleteEntry(id);
        return ResponseEntity.ok(Map.of("message", "Entry deleted successfully"));
    }

    /**
     * GET /api/entries/{id}/score — Get the final calculated score for an entry.
     * Business Rule #1: Final score = average of all submitted scorecards.
     */
    @GetMapping("/{id}/score")
    public ResponseEntity<Map<String, Object>> getEntryScore(@PathVariable Long id) {
        return ResponseEntity.ok(scoreCardService.calculateFinalScore(id));
    }

    /**
     * GET /api/entries/leaderboard — Get all entries ranked by average score.
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard() {
        return ResponseEntity.ok(scoreCardService.getLeaderboard());
    }
}
