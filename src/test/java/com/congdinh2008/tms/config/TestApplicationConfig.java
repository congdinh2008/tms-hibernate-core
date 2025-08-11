package com.congdinh2008.tms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

/**
 * Test configuration to provide mock Scanner for testing
 * This prevents NoSuchElementException during CLI tests
 */
@Configuration
@Profile("test")
public class TestApplicationConfig {

    /**
     * Provides a mock Scanner with predefined input for testing
     * This prevents CLI tests from hanging or throwing NoSuchElementException
     */
    @Bean
    @Primary
    public Scanner testScanner() {
        // Provide mock input that simulates user choices
        String mockInput = "0\n0\n0\n0\n0\n0\n0\n0\n0\n0\n"; // Multiple "0" (exit) choices
        ByteArrayInputStream inputStream = new ByteArrayInputStream(mockInput.getBytes());
        return new Scanner(inputStream);
    }
}
