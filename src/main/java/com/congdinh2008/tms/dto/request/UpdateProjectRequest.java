package com.congdinh2008.tms.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * Request DTO for updating an existing project
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class UpdateProjectRequest {
    
    @Size(min = 1, max = 255, message = "Project name must be between 1 and 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Set<Long> memberIds;
    
    // Constructors
    public UpdateProjectRequest() {}
    
    public UpdateProjectRequest(String name, String description, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Set<Long> getMemberIds() {
        return memberIds;
    }
    
    public void setMemberIds(Set<Long> memberIds) {
        this.memberIds = memberIds;
    }
}
