package com.congdinh2008.tms.cli;

/**
 * Menu Service interface for CLI operations
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
public interface MenuService {
    
    /**
     * Display main menu options
     */
    void displayMainMenu();
    
    /**
     * Handle project management operations
     */
    void handleProjectOperations();
    
    /**
     * Handle task management operations
     */
    void handleTaskOperations();
    
    /**
     * Handle user management operations
     */
    void handleUserOperations();
    
    /**
     * Handle tag management operations
     */
    void handleTagOperations();
    
    /**
     * Handle report and statistics operations
     */
    void handleReportOperations();
    
    /**
     * Handle advanced features demonstration
     */
    void handleAdvancedFeaturesDemo();
}
