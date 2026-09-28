package com.filmjury.exception;

/**
 * Thrown when a judge attempts to submit a duplicate scorecard
 * for the same entry (Business Rule #2).
 */
public class DuplicateScorecardException extends RuntimeException {

    public DuplicateScorecardException(String message) {
        super(message);
    }
}
