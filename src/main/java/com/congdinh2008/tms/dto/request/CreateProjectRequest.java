package com.congdinh2008.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * Request DTO for creating a new project
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class CreateProjectRequest {
    
    @NotBlank(message = "Project name is required")
    @Size(min = 1, max = 255, message = "Project name must be between 1 and 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    private Set<Long> memberIds;
    
    // Constructors
    public CreateProjectRequest() {}
    
    public CreateProjectRequest(String name, String description, LocalDate startDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
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
    
    public Set<Long> getMemberIds() {
        return memberIds;
    }
    
    public void setMemberIds(Set<Long> memberIds) {
        this.memberIds = memberIds;
    }
}
