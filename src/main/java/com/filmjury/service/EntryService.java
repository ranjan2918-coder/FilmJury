package com.filmjury.service;

import com.filmjury.entity.Entry;
import com.filmjury.exception.ResourceNotFoundException;
import com.filmjury.repository.EntryRepository;
import com.filmjury.repository.ScoreCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for Entry entity.
 * Handles CRUD operations and business logic for film entries.
 */
@Service
public class EntryService {

    private final EntryRepository entryRepository;
    private final ScoreCardRepository scoreCardRepository;

    public EntryService(EntryRepository entryRepository,
                        ScoreCardRepository scoreCardRepository) {
        this.entryRepository = entryRepository;
        this.scoreCardRepository = scoreCardRepository;
    }

    /**
     * Create a new film entry.
     */
    public Entry createEntry(Entry entry) {
        return entryRepository.save(entry);
    }

    /**
     * Retrieve all film entries.
     */
    public List<Entry> getAllEntries() {
        return entryRepository.findAll();
    }

    /**
     * Retrieve a single entry by ID.
     * @throws ResourceNotFoundException if the entry does not exist.
     */
    public Entry getEntryById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entry not found with id: " + id));
    }

    /**
     * Update an existing entry.
     * @throws ResourceNotFoundException if the entry does not exist.
     */
    public Entry updateEntry(Long id, Entry updatedEntry) {
        Entry existing = getEntryById(id);
        existing.setTitle(updatedEntry.getTitle());
        existing.setGenre(updatedEntry.getGenre());
        existing.setVideoLink(updatedEntry.getVideoLink());
        return entryRepository.save(existing);
    }

    /**
     * Delete an entry by ID.
     * Also deletes all scorecards associated with this entry
     * to prevent foreign key constraint violations.
     * @throws ResourceNotFoundException if the entry does not exist.
     */
    public void deleteEntry(Long id) {
        Entry existing = getEntryById(id);
        // Delete all scorecards referencing this entry first
        scoreCardRepository.deleteAll(scoreCardRepository.findByEntryId(id));
        entryRepository.delete(existing);
    }
}
