package com.filmjury.exception;

/**
 * Thrown when a requested resource (Entry, Judge, Criterion, ScoreCard)
 * cannot be found in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
