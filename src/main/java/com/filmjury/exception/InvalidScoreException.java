package com.filmjury.exception;

/**
 * Thrown when a submitted score violates the criterion's maxScore constraint.
 */
public class InvalidScoreException extends RuntimeException {

    public InvalidScoreException(String message) {
        super(message);
    }
}
