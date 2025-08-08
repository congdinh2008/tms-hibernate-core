package com.congdinh2008.tms.exceptions;

/**
 * Exception thrown when a circular reference is detected
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class CircularReferenceException extends RuntimeException {
    
    private final String entityType;
    private final Object entityId;
    private final Object parentId;
    
    public CircularReferenceException(String entityType, Object entityId, Object parentId) {
        super(String.format("Circular reference detected: %s %s cannot be parent of %s", 
                entityType, parentId, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
        this.parentId = parentId;
    }
    
    public CircularReferenceException(String message) {
        super(message);
        this.entityType = null;
        this.entityId = null;
        this.parentId = null;
    }
    
    public CircularReferenceException(String entityType, Object entityId, Object parentId, String message) {
        super(message);
        this.entityType = entityType;
        this.entityId = entityId;
        this.parentId = parentId;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public Object getEntityId() {
        return entityId;
    }
    
    public Object getParentId() {
        return parentId;
    }
}
