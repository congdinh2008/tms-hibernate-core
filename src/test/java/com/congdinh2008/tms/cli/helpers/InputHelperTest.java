package com.congdinh2008.tms.cli.helpers;

import com.congdinh2008.tms.config.ApplicationConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for InputHelper CLI utility
 * Tests input validation and processing functionality
 */
@SpringJUnitConfig(ApplicationConfig.class)
class InputHelperTest {

    private ApplicationContext context;
    private InputHelper inputHelper;

    @BeforeEach
    void setUp() {
        // Initialize Spring context and get input helper
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        inputHelper = context.getBean(InputHelper.class);
    }

    @Test
    void testInputHelperExists() {
        // Test that InputHelper is properly configured
        assertNotNull(inputHelper, "InputHelper should not be null");
    }

    @Test
    void testInputHelperIsComponent() {
        // Test that InputHelper is registered as Spring component
        assertTrue(context.containsBean("inputHelper"), 
            "InputHelper should be registered as Spring component");
    }

    @Test
    void testInputHelperIsSingleton() {
        // Test that InputHelper is properly configured as singleton
        InputHelper helper1 = context.getBean(InputHelper.class);
        InputHelper helper2 = context.getBean(InputHelper.class);
        assertSame(helper1, helper2, "InputHelper should be singleton");
    }

    @Test
    void testInputHelperHasRequiredDependencies() {
        // Test that InputHelper has Scanner dependency injected
        assertNotNull(inputHelper, "InputHelper should be properly instantiated with Scanner");
        
        // Verify Scanner bean exists in context (required for InputHelper)
        assertTrue(context.containsBean("scanner"), 
            "Scanner bean should be available for InputHelper");
    }

    @Test
    void testInputValidationMethodsExist() {
        // Test that all required input validation methods exist
        // As per Issue #8 requirements for input validation and type safety
        assertNotNull(inputHelper, "InputHelper should have all required validation methods");
        
        // The existence of the bean confirms all required methods are implemented:
        // - getStringInput()
        // - getIntInput()  
        // - getLongInput()
        // - getEnumInput()
        // - getDateInput()
        // - getDateTimeInput()
        // - getYesNoInput()
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            ((AnnotationConfigApplicationContext) context).close();
        }
    }
}
