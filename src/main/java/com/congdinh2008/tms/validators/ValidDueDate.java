package com.congdinh2008.tms.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure due date is after project start date
 * 
 * @author Cong Dinh
 * @version 1.0.0
 * @since 1.0.0
 */
@Documented
@Constraint(validatedBy = ValidDueDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDueDate {
    
    String message() default "Due date must be after project start date";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
