package com.filmjury.repository;

import com.filmjury.entity.Criterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Criterion entity CRUD and custom queries.
 */
@Repository
public interface CriterionRepository extends JpaRepository<Criterion, Long> {

    /**
     * Check if a criterion with the given name already exists.
     * Used by the DataSeeder for idempotent seeding.
     */
    boolean existsByName(String name);
}
