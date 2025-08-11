package com.congdinh2008.tms.cli;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.congdinh2008.tms.cli.helpers.DisplayHelper;
import com.congdinh2008.tms.cli.helpers.InputHelper;

/**
 * Simple unit tests for CLI helpers without Spring context
 * Testing Issue #8 CLI requirements - Basic functionality tests
 */
class SimpleMenuServiceTest {

    private DisplayHelper displayHelper;
    private InputHelper inputHelper;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        displayHelper = new DisplayHelper();
        Scanner scanner = new Scanner(System.in);
        inputHelper = new InputHelper(scanner);
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testDisplayHelperNotNull() {
        // Test that DisplayHelper can be instantiated
        assertNotNull(displayHelper);
    }

    @Test
    void testInputHelperNotNull() {
        // Test that InputHelper can be instantiated
        assertNotNull(inputHelper);
    }

    @Test
    void testDisplayHelperSuccess() {
        // Test success message display
        String testMessage = "Test Success Message";
        displayHelper.displaySuccess(testMessage);
        
        String output = outputStream.toString();
        assertTrue(output.contains(testMessage));
    }

    @Test
    void testDisplayHelperError() {
        // Test error message display
        String testMessage = "Test Error Message";
        displayHelper.displayError(testMessage);
        
        String output = outputStream.toString();
        assertTrue(output.contains(testMessage));
    }

    @Test
    void testDisplayHelperWarning() {
        // Test warning message display
        String testMessage = "Test Warning Message";
        displayHelper.displayWarning(testMessage);
        
        String output = outputStream.toString();
        assertTrue(output.contains(testMessage));
    }

    @Test
    void testDisplayHelperInfo() {
        // Test info message display
        String testMessage = "Test Info Message";
        displayHelper.displayInfo(testMessage);
        
        String output = outputStream.toString();
        assertTrue(output.contains(testMessage));
    }

    @Test
    void testDisplayHelperHasProjectMethods() {
        // Test that DisplayHelper has project display methods
        assertNotNull(displayHelper);
        
        // Verify methods exist using reflection
        try {
            displayHelper.getClass().getMethod("displayProject", com.congdinh2008.tms.entities.Project.class);
            displayHelper.getClass().getMethod("displayProjectList", java.util.List.class);
        } catch (NoSuchMethodException e) {
            fail("DisplayHelper should have project display methods");
        }
    }

    @Test
    void testDisplayHelperHasTaskMethods() {
        // Test that DisplayHelper has task display methods
        try {
            displayHelper.getClass().getMethod("displayTask", com.congdinh2008.tms.entities.Task.class);
            displayHelper.getClass().getMethod("displayTaskList", java.util.List.class);
        } catch (NoSuchMethodException e) {
            fail("DisplayHelper should have task display methods");
        }
    }

    @Test
    void testDisplayHelperHasUserMethods() {
        // Test that DisplayHelper has user display methods
        try {
            displayHelper.getClass().getMethod("displayUser", com.congdinh2008.tms.entities.User.class);
            displayHelper.getClass().getMethod("displayUserList", java.util.List.class);
        } catch (NoSuchMethodException e) {
            fail("DisplayHelper should have user display methods");
        }
    }

    @Test
    void testDisplayHelperHasTagMethods() {
        // Test that DisplayHelper has tag display methods
        try {
            displayHelper.getClass().getMethod("displayTag", com.congdinh2008.tms.entities.Tag.class);
            displayHelper.getClass().getMethod("displayTagList", java.util.List.class);
        } catch (NoSuchMethodException e) {
            fail("DisplayHelper should have tag display methods");
        }
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }
}
