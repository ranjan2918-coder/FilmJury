package com.filmjury.config;

import com.filmjury.entity.Criterion;
import com.filmjury.entity.Entry;
import com.filmjury.entity.Judge;
import com.filmjury.entity.ScoreCard;
import com.filmjury.repository.CriterionRepository;
import com.filmjury.repository.EntryRepository;
import com.filmjury.repository.JudgeRepository;
import com.filmjury.repository.ScoreCardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the database with sample data on application startup.
 *
 * IMPORTANT: This seeder is IDEMPOTENT.
 * Running the application multiple times will NOT create duplicate records.
 * Each entity is checked for existence before insertion.
 *
 * The seeded scorecards obey the same business rules as normal API requests.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final EntryRepository entryRepository;
    private final JudgeRepository judgeRepository;
    private final CriterionRepository criterionRepository;
    private final ScoreCardRepository scoreCardRepository;

    public DataSeeder(EntryRepository entryRepository,
                      JudgeRepository judgeRepository,
                      CriterionRepository criterionRepository,
                      ScoreCardRepository scoreCardRepository) {
        this.entryRepository = entryRepository;
        this.judgeRepository = judgeRepository;
        this.criterionRepository = criterionRepository;
        this.scoreCardRepository = scoreCardRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("========================================");
        System.out.println(" FilmJury DataSeeder — Starting...");
        System.out.println("========================================");

        // Seed Entries (idempotent — check by title)
        Entry entry1 = seedEntry("The Last Letter", "Drama",
                "https://example.com/the-last-letter");
        Entry entry2 = seedEntry("Beyond the Rain", "Inspirational",
                "https://example.com/beyond-the-rain");
        Entry entry3 = seedEntry("Final Frame", "Thriller",
                "https://example.com/final-frame");

        // Seed Judges (idempotent — check by email)
        Judge judge1 = seedJudge("Arun Kumar", "arun@example.com");
        Judge judge2 = seedJudge("Priya Sharma", "priya@example.com");
        Judge judge3 = seedJudge("Karthik Raj", "karthik@example.com");

        // Seed Criteria (idempotent — check by name)
        Criterion cStory = seedCriterion("Story",
                "Quality and originality of the story", 100);
        Criterion cDirection = seedCriterion("Direction",
                "Quality of direction and visual storytelling", 100);
        Criterion cActing = seedCriterion("Acting",
                "Performance quality of the cast", 100);
        Criterion cCinematography = seedCriterion("Cinematography",
                "Quality of camera work and visual composition", 100);
        Criterion cEditing = seedCriterion("Editing",
                "Quality of editing, pacing, and transitions", 100);

        // Seed ScoreCards (idempotent — check by judge + entry)
        // Using "Story" criterion for all scores to demonstrate the scoring system
        // Each judge scores each entry exactly once

        // Entry 1: "The Last Letter"
        // Judge 1 → 80, Judge 2 → 90, Judge 3 → 70
        // Expected average: (80 + 90 + 70) / 3 = 80.0
        seedScoreCard(judge1, entry1, cStory, 80);
        seedScoreCard(judge2, entry1, cStory, 90);
        seedScoreCard(judge3, entry1, cStory, 70);

        // Entry 2: "Beyond the Rain"
        // Judge 1 → 95, Judge 2 → 88, Judge 3 → 92
        // Expected average: (95 + 88 + 92) / 3 = 91.67 → 91.7
        seedScoreCard(judge1, entry2, cStory, 95);
        seedScoreCard(judge2, entry2, cStory, 88);
        seedScoreCard(judge3, entry2, cStory, 92);

        // Entry 3: "Final Frame"
        // Judge 1 → 75, Judge 2 → 82, Judge 3 → 78
        // Expected average: (75 + 82 + 78) / 3 = 78.33 → 78.3
        seedScoreCard(judge1, entry3, cStory, 75);
        seedScoreCard(judge2, entry3, cStory, 82);
        seedScoreCard(judge3, entry3, cStory, 78);

        System.out.println("========================================");
        System.out.println(" FilmJury DataSeeder — Complete!");
        System.out.println("========================================");
    }

    /**
     * Seed an Entry if it doesn't already exist (checked by title).
     */
    private Entry seedEntry(String title, String genre, String videoLink) {
        if (entryRepository.existsByTitle(title)) {
            System.out.println("  [SKIP] Entry already exists: " + title);
            // Retrieve the existing entry
            return entryRepository.findAll().stream()
                    .filter(e -> e.getTitle().equals(title))
                    .findFirst()
                    .orElse(null);
        }
        Entry entry = entryRepository.save(new Entry(title, genre, videoLink));
        System.out.println("  [SEED] Created Entry: " + title);
        return entry;
    }

    /**
     * Seed a Judge if it doesn't already exist (checked by email).
     */
    private Judge seedJudge(String name, String email) {
        if (judgeRepository.existsByEmail(email)) {
            System.out.println("  [SKIP] Judge already exists: " + name);
            return judgeRepository.findAll().stream()
                    .filter(j -> j.getEmail().equals(email))
                    .findFirst()
                    .orElse(null);
        }
        Judge judge = judgeRepository.save(new Judge(name, email));
        System.out.println("  [SEED] Created Judge: " + name);
        return judge;
    }

    /**
     * Seed a Criterion if it doesn't already exist (checked by name).
     */
    private Criterion seedCriterion(String name, String description, Integer maxScore) {
        if (criterionRepository.existsByName(name)) {
            System.out.println("  [SKIP] Criterion already exists: " + name);
            return criterionRepository.findAll().stream()
                    .filter(c -> c.getName().equals(name))
                    .findFirst()
                    .orElse(null);
        }
        Criterion criterion = criterionRepository.save(new Criterion(name, description, maxScore));
        System.out.println("  [SEED] Created Criterion: " + name);
        return criterion;
    }

    /**
     * Seed a ScoreCard if the judge hasn't already scored the entry.
     * Obeys the same business rules as API submissions.
     */
    private void seedScoreCard(Judge judge, Entry entry, Criterion criterion, Integer score) {
        if (judge == null || entry == null || criterion == null) {
            System.out.println("  [SKIP] ScoreCard — null reference, skipping.");
            return;
        }

        // Business Rule #2: Check for existing scorecard
        if (scoreCardRepository.existsByJudgeIdAndEntryId(judge.getId(), entry.getId())) {
            System.out.println("  [SKIP] ScoreCard already exists: "
                    + judge.getName() + " → " + entry.getTitle());
            return;
        }

        // Business Rule: Validate score against criterion maxScore
        if (score < 0 || score > criterion.getMaxScore()) {
            System.out.println("  [ERROR] Invalid score " + score
                    + " for criterion " + criterion.getName()
                    + " (max: " + criterion.getMaxScore() + ")");
            return;
        }

        ScoreCard scoreCard = new ScoreCard(score, entry, judge, criterion);
        scoreCardRepository.save(scoreCard);
        System.out.println("  [SEED] ScoreCard: " + judge.getName()
                + " → " + entry.getTitle() + " = " + score);
    }
}
