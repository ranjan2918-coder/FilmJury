package com.filmjury.service;

import com.filmjury.entity.Criterion;
import com.filmjury.entity.Entry;
import com.filmjury.entity.Judge;
import com.filmjury.entity.ScoreCard;
import com.filmjury.exception.DuplicateScorecardException;
import com.filmjury.exception.InvalidScoreException;
import com.filmjury.exception.ResourceNotFoundException;
import com.filmjury.repository.CriterionRepository;
import com.filmjury.repository.EntryRepository;
import com.filmjury.repository.JudgeRepository;
import com.filmjury.repository.ScoreCardRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for ScoreCard entity.
 *
 * THIS CLASS CONTAINS THE CORE BUSINESS LOGIC:
 *
 * Business Rule #1: Final score = average of all submitted scorecards for an entry.
 * Business Rule #2: A judge can submit only ONE scorecard per entry per criterion.
 *
 * All validation and business rules are enforced HERE in the service layer,
 * NOT in the controller.
 */
@Service
public class ScoreCardService {

    private final ScoreCardRepository scoreCardRepository;
    private final EntryRepository entryRepository;
    private final JudgeRepository judgeRepository;
    private final CriterionRepository criterionRepository;

    public ScoreCardService(ScoreCardRepository scoreCardRepository,
                            EntryRepository entryRepository,
                            JudgeRepository judgeRepository,
                            CriterionRepository criterionRepository) {
        this.scoreCardRepository = scoreCardRepository;
        this.entryRepository = entryRepository;
        this.judgeRepository = judgeRepository;
        this.criterionRepository = criterionRepository;
    }

    /**
     * Submit a new scorecard.
     *
     * Performs the following checks in order:
     * 1. Entry must exist.
     * 2. Judge must exist.
     * 3. Criterion must exist.
     * 4. Score must be between 0 and criterion's maxScore.
     * 5. Judge must NOT have already submitted a scorecard for this entry.
     *
     * @param entryId     the ID of the entry being scored
     * @param judgeId     the ID of the judge submitting the score
     * @param criterionId the ID of the criterion being scored
     * @param score       the numeric score value
     * @return the saved ScoreCard
     * @throws ResourceNotFoundException   if entry, judge, or criterion not found
     * @throws InvalidScoreException       if score is out of valid range
     * @throws DuplicateScorecardException if judge already scored this entry
     */
    public ScoreCard submitScoreCard(Long entryId, Long judgeId, Long criterionId, Integer score) {

        // 1. Check that the entry exists
        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entry not found with id: " + entryId));

        // 2. Check that the judge exists
        Judge judge = judgeRepository.findById(judgeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Judge not found with id: " + judgeId));

        // 3. Check that the criterion exists
        Criterion criterion = criterionRepository.findById(criterionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Criterion not found with id: " + criterionId));

        // 4. Validate score range: 0 <= score <= criterion.maxScore
        if (score < 0 || score > criterion.getMaxScore()) {
            throw new InvalidScoreException(
                    "Score must be between 0 and " + criterion.getMaxScore()
                            + " (criterion: " + criterion.getName() + ")");
        }

        // 5. BUSINESS RULE #2: Check for duplicate scorecard
        //    A judge can submit only ONE scorecard per entry
        boolean alreadyScored = scoreCardRepository
                .existsByJudgeIdAndEntryId(judgeId, entryId);

        if (alreadyScored) {
            throw new DuplicateScorecardException(
                    "Judge has already submitted a scorecard for this entry.");
        }

        // All checks passed — save the scorecard
        ScoreCard scoreCard = new ScoreCard(score, entry, judge, criterion);
        return scoreCardRepository.save(scoreCard);
    }

    /**
     * Retrieve all scorecards.
     */
    public List<ScoreCard> getAllScoreCards() {
        return scoreCardRepository.findAll();
    }

    /**
     * Retrieve a single scorecard by ID.
     */
    public ScoreCard getScoreCardById(Long id) {
        return scoreCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ScoreCard not found with id: " + id));
    }

    /**
     * Retrieve all scorecards for a specific entry.
     */
    public List<ScoreCard> getScoreCardsByEntryId(Long entryId) {
        // Verify entry exists
        entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entry not found with id: " + entryId));

        return scoreCardRepository.findByEntryId(entryId);
    }

    /**
     * BUSINESS RULE #1: Calculate the final score for an entry.
     *
     * Final Score = average of all submitted scorecard scores for that entry.
     *
     * Example:
     *   Judge 1 → 80
     *   Judge 2 → 90
     *   Judge 3 → 70
     *   Final Score = (80 + 90 + 70) / 3 = 80.0
     *
     * @param entryId the ID of the entry
     * @return a Map containing entryId, title, finalScore, numberOfJudges
     */
    public Map<String, Object> calculateFinalScore(Long entryId) {
        // Verify entry exists
        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entry not found with id: " + entryId));

        // Get all scorecards for this entry
        List<ScoreCard> scoreCards = scoreCardRepository.findByEntryId(entryId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("entryId", entry.getId());
        result.put("title", entry.getTitle());

        if (scoreCards.isEmpty()) {
            result.put("finalScore", 0.0);
            result.put("numberOfJudges", 0);
            result.put("message", "No scores submitted yet for this entry.");
        } else {
            // Calculate average score
            double average = scoreCards.stream()
                    .mapToInt(ScoreCard::getScore)
                    .average()
                    .orElse(0.0);

            // Count distinct judges
            long distinctJudges = scoreCards.stream()
                    .map(sc -> sc.getJudge().getId())
                    .distinct()
                    .count();

            // Round to 1 decimal place
            double finalScore = Math.round(average * 10.0) / 10.0;

            result.put("finalScore", finalScore);
            result.put("numberOfJudges", distinctJudges);
        }

        return result;
    }

    /**
     * Generate a leaderboard: entries ranked by their average score (descending).
     *
     * The ranking is calculated from actual ScoreCard records in the database.
     *
     * @return a list of maps, each containing entryId, title, and finalScore
     */
    public List<Map<String, Object>> getLeaderboard() {
        // Get all entries
        List<Entry> allEntries = entryRepository.findAll();

        List<Map<String, Object>> leaderboard = new ArrayList<>();

        for (Entry entry : allEntries) {
            List<ScoreCard> scoreCards = scoreCardRepository.findByEntryId(entry.getId());

            Map<String, Object> entryData = new LinkedHashMap<>();
            entryData.put("entryId", entry.getId());
            entryData.put("title", entry.getTitle());
            entryData.put("genre", entry.getGenre());

            if (scoreCards.isEmpty()) {
                entryData.put("finalScore", 0.0);
                entryData.put("numberOfJudges", 0);
            } else {
                double average = scoreCards.stream()
                        .mapToInt(ScoreCard::getScore)
                        .average()
                        .orElse(0.0);

                long distinctJudges = scoreCards.stream()
                        .map(sc -> sc.getJudge().getId())
                        .distinct()
                        .count();

                entryData.put("finalScore", Math.round(average * 10.0) / 10.0);
                entryData.put("numberOfJudges", distinctJudges);
            }

            leaderboard.add(entryData);
        }

        // Sort by finalScore in descending order
        leaderboard.sort((a, b) -> {
            Double scoreA = (Double) a.get("finalScore");
            Double scoreB = (Double) b.get("finalScore");
            return scoreB.compareTo(scoreA);
        });

        // Add rank
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).put("rank", i + 1);
        }

        return leaderboard;
    }
}
