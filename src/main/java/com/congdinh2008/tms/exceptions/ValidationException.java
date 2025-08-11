package com.congdinh2008.tms.exceptions;

import java.util.List;

/**
 * Exception thrown when validation fails with multiple error messages
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class ValidationException extends RuntimeException {
    
    private final List<String> errors;
    
    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    @Override
    public String getMessage() {
        if (errors != null && !errors.isEmpty()) {
            return super.getMessage() + ": " + String.join(", ", errors);
        }
        return super.getMessage();
    }
}
