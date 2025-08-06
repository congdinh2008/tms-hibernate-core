package com.congdinh2008.tms.exceptions;

/**
 * Exception thrown when optimistic locking fails
 */
public class OptimisticLockingException extends RepositoryException {
    
    public OptimisticLockingException(String entityName, Object id) {
        super(String.format("Optimistic locking failure for %s with id %s. " +
              "The entity was modified by another transaction.", entityName, id));
    }
    
    public OptimisticLockingException(String message) {
        super(message);
    }
}
