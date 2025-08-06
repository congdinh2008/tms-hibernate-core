package com.congdinh2008.tms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for Task Management System
 */
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    
    public static void main(String[] args) {
        logger.info("Starting Task Management System...");
        System.out.println("Task Management System - Version 1.0.0-SNAPSHOT");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        logger.info("Application started successfully");
    }
}
