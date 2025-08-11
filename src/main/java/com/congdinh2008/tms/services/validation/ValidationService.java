package com.congdinh2008.tms.services.validation;

import com.congdinh2008.tms.dto.request.CreateUserRequest;
import com.congdinh2008.tms.dto.request.UpdateUserRequest;
import com.congdinh2008.tms.dto.request.CreateProjectRequest;
import com.congdinh2008.tms.dto.request.UpdateProjectRequest;
import com.congdinh2008.tms.dto.request.CreateTaskRequest;
import com.congdinh2008.tms.dto.request.UpdateTaskRequest;
import com.congdinh2008.tms.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Centralized validation service for business rules and data validation
 * Reduces code duplication across service layers
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class ValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Password strength pattern (at least 8 characters, containing letters and numbers)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$"
    );
    
    /**
     * Validate CreateUserRequest
     */
    public void validateCreateUserRequest(CreateUserRequest request) {
        logger.debug("Validating CreateUserRequest for email: {}", request.getEmail());
        
        List<String> errors = new ArrayList<>();
        
        // Validate email
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!isValidEmail(request.getEmail())) {
            errors.add("Email format is invalid");
        }
        
        // Validate full name
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            errors.add("Full name is required");
        } else if (request.getFullName().trim().length() < 2) {
            errors.add("Full name must be at least 2 characters long");
        } else if (request.getFullName().trim().length() > 100) {
            errors.add("Full name must not exceed 100 characters");
        }
        
        // Validate password
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            errors.add("Password is required");
        } else if (!isValidPassword(request.getPassword())) {
            errors.add("Password must be at least 8 characters long and contain both letters and numbers");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("User validation failed", errors);
        }
    }
    
    /**
     * Validate UpdateUserRequest
     */
    public void validateUpdateUserRequest(UpdateUserRequest request) {
        logger.debug("Validating UpdateUserRequest");
        
        List<String> errors = new ArrayList<>();
        
        // Validate email if provided
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && !isValidEmail(request.getEmail())) {
            errors.add("Email format is invalid");
        }
        
        // Validate full name if provided
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            if (request.getFullName().trim().length() < 2) {
                errors.add("Full name must be at least 2 characters long");
            } else if (request.getFullName().trim().length() > 100) {
                errors.add("Full name must not exceed 100 characters");
            }
        }
        
        // Validate password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty() && !isValidPassword(request.getPassword())) {
            errors.add("Password must be at least 8 characters long and contain both letters and numbers");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("User update validation failed", errors);
        }
    }
    
    /**
     * Validate CreateProjectRequest
     */
    public void validateCreateProjectRequest(CreateProjectRequest request) {
        logger.debug("Validating CreateProjectRequest for project: {}", request.getName());
        
        List<String> errors = new ArrayList<>();
        
        // Validate project name
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            errors.add("Project name is required");
        } else if (request.getName().trim().length() < 3) {
            errors.add("Project name must be at least 3 characters long");
        } else if (request.getName().trim().length() > 100) {
            errors.add("Project name must not exceed 100 characters");
        }
        
        // Validate description
        if (request.getDescription() != null && request.getDescription().length() > 500) {
            errors.add("Project description must not exceed 500 characters");
        }
        
        // Validate start date
        if (request.getStartDate() != null && request.getStartDate().isBefore(LocalDate.now().minusYears(1))) {
            errors.add("Project start date cannot be more than 1 year in the past");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Project validation failed", errors);
        }
    }
    
    /**
     * Validate UpdateProjectRequest
     */
    public void validateUpdateProjectRequest(UpdateProjectRequest request) {
        logger.debug("Validating UpdateProjectRequest");
        
        List<String> errors = new ArrayList<>();
        
        // Validate project name if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            if (request.getName().trim().length() < 3) {
                errors.add("Project name must be at least 3 characters long");
            } else if (request.getName().trim().length() > 100) {
                errors.add("Project name must not exceed 100 characters");
            }
        }
        
        // Validate description if provided
        if (request.getDescription() != null && request.getDescription().length() > 500) {
            errors.add("Project description must not exceed 500 characters");
        }
        
        // Validate start date if provided
        if (request.getStartDate() != null && request.getStartDate().isBefore(LocalDate.now().minusYears(1))) {
            errors.add("Project start date cannot be more than 1 year in the past");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Project update validation failed", errors);
        }
    }
    
    /**
     * Validate CreateTaskRequest
     */
    public void validateCreateTaskRequest(CreateTaskRequest request) {
        logger.debug("Validating CreateTaskRequest for task: {}", request.getTitle());
        
        List<String> errors = new ArrayList<>();
        
        // Validate task title
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            errors.add("Task title is required");
        } else if (request.getTitle().trim().length() < 3) {
            errors.add("Task title must be at least 3 characters long");
        } else if (request.getTitle().trim().length() > 200) {
            errors.add("Task title must not exceed 200 characters");
        }
        
        // Validate description
        if (request.getDescription() != null && request.getDescription().length() > 1000) {
            errors.add("Task description must not exceed 1000 characters");
        }
        
        // Validate due date
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDate.now())) {
            errors.add("Task due date cannot be in the past");
        }
        
        // Validate project ID
        if (request.getProjectId() == null || request.getProjectId() <= 0) {
            errors.add("Valid project ID is required");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Task validation failed", errors);
        }
    }
    
    /**
     * Validate UpdateTaskRequest
     */
    public void validateUpdateTaskRequest(UpdateTaskRequest request) {
        logger.debug("Validating UpdateTaskRequest");
        
        List<String> errors = new ArrayList<>();
        
        // Validate task title if provided
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            if (request.getTitle().trim().length() < 3) {
                errors.add("Task title must be at least 3 characters long");
            } else if (request.getTitle().trim().length() > 200) {
                errors.add("Task title must not exceed 200 characters");
            }
        }
        
        // Validate description if provided
        if (request.getDescription() != null && request.getDescription().length() > 1000) {
            errors.add("Task description must not exceed 1000 characters");
        }
        
        // Validate due date if provided (allow past dates for existing tasks)
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDate.now().minusYears(1))) {
            errors.add("Task due date cannot be more than 1 year in the past");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Task update validation failed", errors);
        }
    }
    
    /**
     * Validate email format
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate password strength
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Validate date range
     */
    public boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return true; // null dates are handled elsewhere
        }
        return !startDate.isAfter(endDate);
    }
    
    /**
     * Validate ID is positive
     */
    public boolean isValidId(Long id) {
        return id != null && id > 0;
    }
}
