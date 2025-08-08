package com.congdinh2008.tms.exceptions;

/**
 * Exception thrown when a business rule is violated
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class BusinessRuleViolationException extends RuntimeException {
    
    private final String ruleCode;
    
    public BusinessRuleViolationException(String ruleCode, String message) {
        super(message);
        this.ruleCode = ruleCode;
    }
    
    public BusinessRuleViolationException(String ruleCode, String message, Throwable cause) {
        super(message, cause);
        this.ruleCode = ruleCode;
    }
    
    public String getRuleCode() {
        return ruleCode;
    }
}
