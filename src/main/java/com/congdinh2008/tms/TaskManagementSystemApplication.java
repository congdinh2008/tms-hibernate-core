package com.congdinh2008.tms;

import com.congdinh2008.tms.cli.MenuService;
import com.congdinh2008.tms.cli.SampleDataService;
import com.congdinh2008.tms.cli.exception.CLIExceptionHandler;
import com.congdinh2008.tms.cli.helpers.InputHelper;
import com.congdinh2008.tms.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * Task Management System CLI Application
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Component
public class TaskManagementSystemApplication {
    
    private static final Logger log = LoggerFactory.getLogger(TaskManagementSystemApplication.class);
    
    private final MenuService menuService;
    private final Scanner scanner;
    private final InputHelper inputHelper;
    private final CLIExceptionHandler exceptionHandler;
    private final SampleDataService sampleDataService;
    
    @Autowired
    public TaskManagementSystemApplication(MenuService menuService, 
                                         Scanner scanner,
                                         InputHelper inputHelper,
                                         CLIExceptionHandler exceptionHandler,
                                         SampleDataService sampleDataService) {
        this.menuService = menuService;
        this.scanner = scanner;
        this.inputHelper = inputHelper;
        this.exceptionHandler = exceptionHandler;
        this.sampleDataService = sampleDataService;
    }
    
    public static void main(String[] args) {
        try {
            ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
            TaskManagementSystemApplication app = context.getBean(TaskManagementSystemApplication.class);
            app.run();
        } catch (Exception e) {
            log.error("Application failed to start", e);
            System.err.println("❌ Ứng dụng không thể khởi động: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void run() {
        displayWelcome();
        
        // Hỏi có muốn tải dữ liệu mẫu không
        if (inputHelper.getYesNoInput("Bạn có muốn tải dữ liệu mẫu để demo không? (y/n): ")) {
            sampleDataService.loadSampleData();
        }
        
        while (true) {
            try {
                menuService.displayMainMenu();
                int choice = inputHelper.getIntInput("Chọn chức năng: ");
                handleMainMenu(choice);
            } catch (Exception e) {
                exceptionHandler.handleException(e);
                
                // Hỏi có muốn tiếp tục không
                if (!inputHelper.getYesNoInput("Bạn có muốn tiếp tục không? (y/n): ")) {
                    break;
                }
            }
        }
        
        System.out.println("\n👋 Cảm ơn bạn đã sử dụng Task Management System!");
        System.exit(0);
    }
    
    private void displayWelcome() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 CHÀO MỪNG BẠN ĐẾN VỚI TASK MANAGEMENT SYSTEM");
        System.out.println("   Hệ thống quản lý công việc với Hibernate & Spring");
        System.out.println("=".repeat(60));
        System.out.println("📋 Phiên bản: 1.0.0-SNAPSHOT");
        System.out.println("⚡ Java Version: " + System.getProperty("java.version"));
        System.out.println("🌱 Spring Framework: Enabled");
        System.out.println("🗄️  Hibernate ORM: Enabled");
        System.out.println("=".repeat(60));
        System.out.println();
    }
    
    private void handleMainMenu(int choice) {
        switch (choice) {
            case 1 -> menuService.handleProjectOperations();
            case 2 -> menuService.handleTaskOperations();
            case 3 -> menuService.handleUserOperations();
            case 4 -> menuService.handleTagOperations();
            case 5 -> menuService.handleReportOperations();
            case 6 -> menuService.handleAdvancedFeaturesDemo();
            case 7 -> sampleDataService.loadSampleData();
            case 0 -> {
                System.out.println("\n👋 Cảm ơn bạn đã sử dụng Task Management System!");
                System.exit(0);
            }
            default -> System.out.println("❌ Lựa chọn không hợp lệ! Vui lòng chọn từ 0-7.");
        }
    }
}
