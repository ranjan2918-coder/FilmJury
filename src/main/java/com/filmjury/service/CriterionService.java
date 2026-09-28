package com.filmjury.service;

import com.filmjury.entity.Criterion;
import com.filmjury.exception.ResourceNotFoundException;
import com.filmjury.repository.CriterionRepository;
import com.filmjury.repository.ScoreCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for Criterion entity.
 * Handles CRUD operations for judging criteria.
 */
@Service
public class CriterionService {

    private final CriterionRepository criterionRepository;
    private final ScoreCardRepository scoreCardRepository;

    public CriterionService(CriterionRepository criterionRepository,
                            ScoreCardRepository scoreCardRepository) {
        this.criterionRepository = criterionRepository;
        this.scoreCardRepository = scoreCardRepository;
    }

    /**
     * Create a new judging criterion.
     */
    public Criterion createCriterion(Criterion criterion) {
        return criterionRepository.save(criterion);
    }

    /**
     * Retrieve all criteria.
     */
    public List<Criterion> getAllCriteria() {
        return criterionRepository.findAll();
    }

    /**
     * Retrieve a single criterion by ID.
     * @throws ResourceNotFoundException if the criterion does not exist.
     */
    public Criterion getCriterionById(Long id) {
        return criterionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Criterion not found with id: " + id));
    }

    /**
     * Update an existing criterion.
     * @throws ResourceNotFoundException if the criterion does not exist.
     */
    public Criterion updateCriterion(Long id, Criterion updatedCriterion) {
        Criterion existing = getCriterionById(id);
        existing.setName(updatedCriterion.getName());
        existing.setDescription(updatedCriterion.getDescription());
        existing.setMaxScore(updatedCriterion.getMaxScore());
        return criterionRepository.save(existing);
    }

    /**
     * Delete a criterion by ID.
     * Also deletes all scorecards using this criterion
     * to prevent foreign key constraint violations.
     * @throws ResourceNotFoundException if the criterion does not exist.
     */
    public void deleteCriterion(Long id) {
        Criterion existing = getCriterionById(id);
        // Delete all scorecards using this criterion first
        scoreCardRepository.deleteAll(scoreCardRepository.findByCriterionId(id));
        criterionRepository.delete(existing);
    }
}
