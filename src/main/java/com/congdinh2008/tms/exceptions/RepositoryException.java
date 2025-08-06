package com.congdinh2008.tms.exceptions;

/**
 * Base exception for repository layer operations
 */
public class RepositoryException extends RuntimeException {
    
    public RepositoryException(String message) {
        super(message);
    }
    
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
