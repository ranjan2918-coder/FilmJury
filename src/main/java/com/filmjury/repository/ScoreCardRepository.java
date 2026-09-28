package com.filmjury.repository;

import com.filmjury.entity.ScoreCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ScoreCard entity with custom query methods
 * to support business rule enforcement.
 */
@Repository
public interface ScoreCardRepository extends JpaRepository<ScoreCard, Long> {

    /**
     * Check if a judge has already submitted a scorecard for a specific entry
     * and criterion. Used to enforce the "one scorecard per judge per entry
     * per criterion" business rule.
     */
    boolean existsByJudgeIdAndEntryIdAndCriterionId(Long judgeId, Long entryId, Long criterionId);

    /**
     * Check if a judge has already submitted ANY scorecard for a specific entry.
     * Used for the simplified "one scorecard per judge per entry" business rule.
     */
    boolean existsByJudgeIdAndEntryId(Long judgeId, Long entryId);

    /**
     * Retrieve all scorecards for a given entry.
     * Used to calculate the final (average) score.
     */
    List<ScoreCard> findByEntryId(Long entryId);

    /**
     * Count the number of distinct judges who have scored a given entry.
     */
    long countDistinctJudgeByEntryId(Long entryId);

    /**
     * Retrieve all scorecards submitted by a specific judge.
     * Used for cascading delete when a judge is removed.
     */
    List<ScoreCard> findByJudgeId(Long judgeId);

    /**
     * Retrieve all scorecards for a specific criterion.
     * Used for cascading delete when a criterion is removed.
     */
    List<ScoreCard> findByCriterionId(Long criterionId);
}
