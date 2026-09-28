package com.filmjury.repository;

import com.filmjury.entity.Judge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Judge entity CRUD and custom queries.
 */
@Repository
public interface JudgeRepository extends JpaRepository<Judge, Long> {

    /**
     * Check if a judge with the given email already exists.
     * Used by the DataSeeder for idempotent seeding.
     */
    boolean existsByEmail(String email);
}
