package com.congdinh2008.tms.validators;

import com.congdinh2008.tms.entities.Task;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidDueDate annotation
 * Validates that task due date is after the project start date
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class ValidDueDateValidator implements ConstraintValidator<ValidDueDate, Task> {

    @Override
    public void initialize(ValidDueDate constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Task task, ConstraintValidatorContext context) {
        if (task == null) {
            return true; // Let @NotNull handle null validation
        }

        if (task.getDueDate() == null || task.getProject() == null || task.getProject().getStartDate() == null) {
            return true; // Let other validators handle null validation
        }

        // Due date must be after or equal to project start date
        boolean isValid = !task.getDueDate().isBefore(task.getProject().getStartDate());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("Due date (%s) must be after project start date (%s)", 
                    task.getDueDate(), task.getProject().getStartDate()))
                .addPropertyNode("dueDate")
                .addConstraintViolation();
        }

        return isValid;
    }
}
