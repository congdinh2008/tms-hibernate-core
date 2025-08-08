package com.congdinh2008.tms.exceptions;

/**
 * Exception thrown when an invalid assignment is attempted
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidAssignmentException extends RuntimeException {
    
    private final String reason;
    
    public InvalidAssignmentException(String reason) {
        super("Invalid assignment: " + reason);
        this.reason = reason;
    }
    
    public InvalidAssignmentException(String reason, String message) {
        super(message);
        this.reason = reason;
    }
    
    public String getReason() {
        return reason;
    }
}
