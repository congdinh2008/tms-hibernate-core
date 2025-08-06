package com.congdinh2008.tms.enums;

/**
 * Enumeration for task priority values
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public enum TaskPriority {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
