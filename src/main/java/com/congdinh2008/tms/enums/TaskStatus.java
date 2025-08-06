package com.congdinh2008.tms.enums;

/**
 * Enumeration for task status values
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public enum TaskStatus {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String displayName;

    TaskStatus(String displayName) {
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
