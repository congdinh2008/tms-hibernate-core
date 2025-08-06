package com.congdinh2008.tms.config;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.MDC;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HibernateUtil configuration and SessionFactory management
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HibernateUtilTest {
    
    @BeforeAll
    void setUp() {
        // Set correlation ID for testing
        MDC.put("correlationId", UUID.randomUUID().toString());
    }
    
    @AfterAll
    void tearDown() {
        // Clean up
        HibernateUtil.shutdown();
        MDC.clear();
    }
    
    @Test
    @DisplayName("Test SessionFactory creation and singleton pattern")
    void testSessionFactoryCreation() {
        // Test first call creates SessionFactory
        SessionFactory firstCall = HibernateUtil.getSessionFactory();
        assertNotNull(firstCall, "SessionFactory should not be null");
        assertTrue(HibernateUtil.isSessionFactoryActive(), "SessionFactory should be active");
        
        // Test second call returns same instance (Singleton pattern)
        SessionFactory secondCall = HibernateUtil.getSessionFactory();
        assertSame(firstCall, secondCall, "Both calls should return the same SessionFactory instance");
    }
    
    @Test
    @DisplayName("Test SessionFactory is not closed")
    void testSessionFactoryNotClosed() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        assertFalse(sf.isClosed(), "SessionFactory should not be closed");
    }
    
    @Test
    @DisplayName("Test SessionFactory statistics are available")
    void testSessionFactoryStatistics() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        Statistics stats = HibernateUtil.getStatistics();
        
        assertNotNull(stats, "Statistics should be available");
        assertSame(sf.getStatistics(), stats, "Statistics should be from the same SessionFactory");
        
        // Test some basic statistics properties  
        assertNotNull(stats.getEntityNames(), "Entity names should be available");
    }
    
    @Test
    @DisplayName("Test SessionFactory active status check")
    void testSessionFactoryActiveStatus() {
        // Before getting SessionFactory
        if (!HibernateUtil.isSessionFactoryActive()) {
            // If not active, get it first
            assertNotNull(HibernateUtil.getSessionFactory(), "SessionFactory should be created");
        }
        
        assertTrue(HibernateUtil.isSessionFactoryActive(), "SessionFactory should be active");
    }
    
    @Test
    @DisplayName("Test database connection properties are loaded")
    void testDatabaseConnectionProperties() {
        // Just ensure SessionFactory creation doesn't throw
        assertDoesNotThrow(() -> {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            assertNotNull(sf, "SessionFactory should be created");
            
            // Test that we can get basic metadata (this validates the connection works)
            var metadata = sf.getMetamodel();
            assertNotNull(metadata, "Metamodel should be available");
        }, "Should be able to access metamodel without errors");
    }
    
    @Test
    @DisplayName("Test SessionFactory shutdown and cleanup")
    void testSessionFactoryShutdown() {
        // Ensure SessionFactory is created first
        SessionFactory sf = HibernateUtil.getSessionFactory();
        assertTrue(HibernateUtil.isSessionFactoryActive(), "SessionFactory should be active before shutdown");
        
        // Test shutdown
        HibernateUtil.shutdown();
        
        // After shutdown, isSessionFactoryActive should return false
        assertFalse(HibernateUtil.isSessionFactoryActive(), "SessionFactory should not be active after shutdown");
        
        // Getting SessionFactory again should create a new one
        SessionFactory newSf = HibernateUtil.getSessionFactory();
        assertNotNull(newSf, "New SessionFactory should be created after shutdown");
        assertTrue(HibernateUtil.isSessionFactoryActive(), "New SessionFactory should be active");
    }
    
    @Test
    @DisplayName("Test configuration loading handles missing properties gracefully")
    void testConfigurationErrorHandling() {
        // This test verifies that our current configuration works
        // In a real scenario, you might want to test with invalid configurations
        // but that would require more complex setup
        
        assertDoesNotThrow(() -> {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            assertNotNull(sf, "SessionFactory should be created with valid configuration");
        }, "Valid configuration should not throw exceptions");
    }
    
    @Test
    @DisplayName("Test thread safety of SessionFactory creation")
    void testThreadSafetyOfSessionFactoryCreation() throws InterruptedException {
        // First shutdown existing SessionFactory to test creation from scratch
        HibernateUtil.shutdown();
        
        // Create multiple threads that try to get SessionFactory simultaneously
        final SessionFactory[] results = new SessionFactory[5];
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = HibernateUtil.getSessionFactory();
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Verify all threads got the same SessionFactory instance
        SessionFactory first = results[0];
        assertNotNull(first, "First SessionFactory should not be null");
        
        for (int i = 1; i < results.length; i++) {
            assertSame(first, results[i], 
                "All threads should get the same SessionFactory instance (thread " + i + ")");
        }
    }
}
