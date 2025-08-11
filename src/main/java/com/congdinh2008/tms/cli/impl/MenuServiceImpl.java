package com.congdinh2008.tms.cli.impl;

import com.congdinh2008.tms.cli.MenuService;
import com.congdinh2008.tms.cli.controllers.*;
import com.congdinh2008.tms.cli.helpers.InputHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of MenuService for CLI operations
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Service
public class MenuServiceImpl implements MenuService {
    
    private final InputHelper inputHelper;
    private final ProjectController projectController;
    private final TaskController taskController;
    private final UserController userController;
    private final TagController tagController;
    private final ReportController reportController;
    private final DemoController demoController;
    
    @Autowired
    public MenuServiceImpl(InputHelper inputHelper,
                          ProjectController projectController,
                          TaskController taskController,
                          UserController userController,
                          TagController tagController,
                          ReportController reportController,
                          DemoController demoController) {
        this.inputHelper = inputHelper;
        this.projectController = projectController;
        this.taskController = taskController;
        this.userController = userController;
        this.tagController = tagController;
        this.reportController = reportController;
        this.demoController = demoController;
    }
    
    @Override
    public void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    TASK MANAGEMENT SYSTEM - HIBERNATE DEMO");
        System.out.println("=".repeat(50));
        System.out.println("1. 📁 Quản lý Dự án (Projects)");
        System.out.println("2. 📋 Quản lý Công việc (Tasks)");
        System.out.println("3. 👥 Quản lý Người dùng (Users)");
        System.out.println("4. 🏷️ Quản lý Tags");
        System.out.println("5. 📊 Báo cáo và Thống kê");
        System.out.println("6. 🚀 Demo các tính năng nâng cao");
        System.out.println("7. 🔄 Tải lại dữ liệu mẫu");
        System.out.println("0. 🚪 Thoát");
        System.out.println("=".repeat(50));
    }
    
    @Override
    public void handleProjectOperations() {
        projectController.handleProjectMenu();
    }
    
    @Override
    public void handleTaskOperations() {
        taskController.handleTaskMenu();
    }
    
    @Override
    public void handleUserOperations() {
        userController.handleUserMenu();
    }
    
    @Override
    public void handleTagOperations() {
        tagController.handleTagMenu();
    }
    
    @Override
    public void handleReportOperations() {
        reportController.handleReportMenu();
    }
    
    @Override
    public void handleAdvancedFeaturesDemo() {
        demoController.handleAdvancedDemo();
    }
}
