package com.congdinh2008.tms.validators;

import com.congdinh2008.tms.entities.Task;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidAssignee annotation
 * Validates that the assigned user is a member of the project
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
public class ValidAssigneeValidator implements ConstraintValidator<ValidAssignee, Task> {

    @Override
    public void initialize(ValidAssignee constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Task task, ConstraintValidatorContext context) {
        if (task == null) {
            return true; // Let @NotNull handle null validation
        }

        // If no assignee, validation passes (assignee is optional)
        if (task.getAssignee() == null) {
            return true;
        }

        // If no project, let other validators handle this
        if (task.getProject() == null) {
            return true;
        }

        // Check if assignee is a member of the project
        boolean isValid = task.getProject().isMember(task.getAssignee());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("User '%s' is not a member of project '%s'", 
                    task.getAssignee().getName(), task.getProject().getName()))
                .addPropertyNode("assignee")
                .addConstraintViolation();
        }

        return isValid;
    }
}
