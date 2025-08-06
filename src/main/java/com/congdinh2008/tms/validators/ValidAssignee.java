package com.congdinh2008.tms.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure assignee is a member of the project
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Documented
@Constraint(validatedBy = ValidAssigneeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAssignee {
    
    String message() default "Assignee must be a member of the project";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
