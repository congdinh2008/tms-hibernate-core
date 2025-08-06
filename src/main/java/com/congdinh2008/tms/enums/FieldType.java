package com.congdinh2008.tms.enums;

/**
 * Enumeration for field types in task history tracking
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public enum FieldType {
    STATUS("Status"),
    ASSIGNEE("Assignee"),
    DUE_DATE("Due Date"),
    PRIORITY("Priority"),
    TITLE("Title"),
    DESCRIPTION("Description"),
    PARENT_TASK("Parent Task"),
    TAGS("Tags");

    private final String displayName;

    FieldType(String displayName) {
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
