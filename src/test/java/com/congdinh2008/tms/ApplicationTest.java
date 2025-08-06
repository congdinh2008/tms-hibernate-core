package com.congdinh2008.tms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test class for Application
 */
class ApplicationTest {
    
    @Test
    @DisplayName("Test application main method execution")
    void testApplicationMainMethod() {
        // Test that main method runs without throwing exceptions
        assertDoesNotThrow(() -> {
            Application.main(new String[]{});
        });
    }
    
    @Test
    @DisplayName("Test Java version")
    void testJavaVersion() {
        String javaVersion = System.getProperty("java.version");
        assertNotNull(javaVersion);
        // Accept both Java 21 and higher versions as acceptable
        assertTrue(javaVersion.startsWith("21") || javaVersion.startsWith("22") || javaVersion.startsWith("23") || javaVersion.startsWith("24"), 
                   "Expected Java 21 or higher, but found: " + javaVersion);
    }
}
