package com.congdinh2008.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new tag
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class CreateTagRequest {
    
    @NotBlank(message = "Tag name is required")
    @Size(min = 1, max = 50, message = "Tag name must be between 1 and 50 characters")
    private String name;
    
    @Size(max = 7, message = "Color must not exceed 7 characters")
    private String color;
    
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
    
    // Constructors
    public CreateTagRequest() {}
    
    public CreateTagRequest(String name) {
        this.name = name;
    }
    
    public CreateTagRequest(String name, String color, String description) {
        this.name = name;
        this.color = color;
        this.description = description;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
