package com.filmjury.service;

import com.filmjury.entity.Judge;
import com.filmjury.exception.ResourceNotFoundException;
import com.filmjury.repository.JudgeRepository;
import com.filmjury.repository.ScoreCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for Judge entity.
 * Handles CRUD operations for judges.
 */
@Service
public class JudgeService {

    private final JudgeRepository judgeRepository;
    private final ScoreCardRepository scoreCardRepository;

    public JudgeService(JudgeRepository judgeRepository,
                        ScoreCardRepository scoreCardRepository) {
        this.judgeRepository = judgeRepository;
        this.scoreCardRepository = scoreCardRepository;
    }

    /**
     * Create a new judge.
     */
    public Judge createJudge(Judge judge) {
        return judgeRepository.save(judge);
    }

    /**
     * Retrieve all judges.
     */
    public List<Judge> getAllJudges() {
        return judgeRepository.findAll();
    }

    /**
     * Retrieve a single judge by ID.
     * @throws ResourceNotFoundException if the judge does not exist.
     */
    public Judge getJudgeById(Long id) {
        return judgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Judge not found with id: " + id));
    }

    /**
     * Update an existing judge.
     * @throws ResourceNotFoundException if the judge does not exist.
     */
    public Judge updateJudge(Long id, Judge updatedJudge) {
        Judge existing = getJudgeById(id);
        existing.setName(updatedJudge.getName());
        existing.setEmail(updatedJudge.getEmail());
        return judgeRepository.save(existing);
    }

    /**
     * Delete a judge by ID.
     * Also deletes all scorecards submitted by this judge
     * to prevent foreign key constraint violations.
     * @throws ResourceNotFoundException if the judge does not exist.
     */
    public void deleteJudge(Long id) {
        Judge existing = getJudgeById(id);
        // Delete all scorecards submitted by this judge first
        scoreCardRepository.deleteAll(scoreCardRepository.findByJudgeId(id));
        judgeRepository.delete(existing);
    }
}
