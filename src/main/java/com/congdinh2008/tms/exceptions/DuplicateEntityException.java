package com.congdinh2008.tms.exceptions;

/**
 * Exception thrown when trying to create a duplicate entity
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class DuplicateEntityException extends RuntimeException {
    
    private final String entityType;
    private final String fieldName;
    private final Object fieldValue;
    
    public DuplicateEntityException(String entityType, String fieldName, Object fieldValue) {
        super(String.format("%s with %s '%s' already exists", entityType, fieldName, fieldValue));
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public DuplicateEntityException(String entityType, String fieldName, Object fieldValue, String message) {
        super(message);
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getFieldValue() {
        return fieldValue;
    }
}
