package com.filmjury.repository;

import com.filmjury.entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Entry entity CRUD and custom queries.
 */
@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {

    /**
     * Check if an entry with the given title already exists.
     * Used by the DataSeeder for idempotent seeding.
     */
    boolean existsByTitle(String title);
}
