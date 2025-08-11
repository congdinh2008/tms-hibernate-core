package com.congdinh2008.tms.cli.controllers;

import com.congdinh2008.tms.cli.exception.CLIExceptionHandler;
import com.congdinh2008.tms.cli.helpers.DisplayHelper;
import com.congdinh2008.tms.cli.helpers.InputHelper;
import com.congdinh2008.tms.dto.request.CreateProjectRequest;
import com.congdinh2008.tms.dto.request.UpdateProjectRequest;
import com.congdinh2008.tms.dto.response.ProjectResponse;
import com.congdinh2008.tms.dto.response.UserResponse;
import com.congdinh2008.tms.services.ProjectService;
import com.congdinh2008.tms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CLI Controller for Project operations
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Component
public class ProjectController {
    
    private final ProjectService projectService;
    private final UserService userService;
    private final InputHelper inputHelper;
    private final DisplayHelper displayHelper;
    private final CLIExceptionHandler exceptionHandler;
    
    @Autowired
    public ProjectController(ProjectService projectService,
                           UserService userService,
                           InputHelper inputHelper,
                           DisplayHelper displayHelper,
                           CLIExceptionHandler exceptionHandler) {
        this.projectService = projectService;
        this.userService = userService;
        this.inputHelper = inputHelper;
        this.displayHelper = displayHelper;
        this.exceptionHandler = exceptionHandler;
    }
    
    /**
     * Handle project menu operations
     */
    public void handleProjectMenu() {
        while (true) {
            displayProjectMenu();
            int choice = inputHelper.getIntInput("Chọn chức năng: ");
            
            switch (choice) {
                case 1 -> createProject();
                case 2 -> viewAllProjects();
                case 3 -> viewProjectDetails();
                case 4 -> updateProject();
                case 5 -> deleteProject();
                case 6 -> addMemberToProject();
                case 7 -> removeMemberFromProject();
                case 8 -> viewProjectTasks();
                case 0 -> { return; }
                default -> System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
    
    /**
     * Display project menu
     */
    private void displayProjectMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("📁 QUẢN LÝ DỰ ÁN");
        System.out.println("=".repeat(40));
        System.out.println("1. ➕ Tạo dự án mới");
        System.out.println("2. 📄 Xem tất cả dự án");
        System.out.println("3. 🔍 Xem chi tiết dự án");
        System.out.println("4. ✏️ Cập nhật dự án");
        System.out.println("5. 🗑️ Xóa dự án");
        System.out.println("6. 👤 Thêm thành viên");
        System.out.println("7. ➖ Xóa thành viên");
        System.out.println("8. 📋 Xem công việc của dự án");
        System.out.println("0. ⬅️ Quay lại");
        System.out.println("=".repeat(40));
    }
    
    /**
     * Create a new project
     */
    private void createProject() {
        try {
            System.out.println("\n--- TẠO DỰ ÁN MỚI ---");
            
            CreateProjectRequest request = new CreateProjectRequest();
            request.setName(inputHelper.getStringInput("Tên dự án: "));
            request.setDescription(inputHelper.getOptionalStringInput("Mô tả (có thể bỏ qua): "));
            request.setStartDate(inputHelper.getDateInput("Ngày bắt đầu"));
            
            ProjectResponse project = projectService.create(request);
            displayHelper.displaySuccess("Tạo dự án thành công!");
            displayHelper.displayProjectResponse(null); // Will need to convert from response to entity for display
            
            // Ask if user wants to add members immediately
            if (inputHelper.getYesNoInput("Bạn có muốn thêm thành viên vào dự án không? (y/n): ")) {
                addMemberToProject(project.getId());
            }
            
        } catch (Exception e) {
            exceptionHandler.handleException(e, "tạo dự án");
        }
    }
    
    /**
     * View all projects
     */
    private void viewAllProjects() {
        try {
            System.out.println("\n--- DANH SÁCH TẤT CẢ DỰ ÁN ---");
            List<ProjectResponse> projects = projectService.getAll();
            
            if (projects.isEmpty()) {
                System.out.println("📭 Chưa có dự án nào trong hệ thống.");
                return;
            }
            
            // Display projects in table format
            System.out.println("\n📁 DANH SÁCH DỰ ÁN");
            System.out.println("─".repeat(60));
            System.out.printf("%-5s %-30s %-15s%n", "ID", "Tên dự án", "Ngày bắt đầu");
            System.out.println("─".repeat(60));
            
            for (ProjectResponse project : projects) {
                System.out.printf("%-5d %-30s %-15s%n",
                    project.getId(),
                    truncate(project.getName(), 30),
                    project.getStartDate() != null ? project.getStartDate().toString() : "N/A"
                );
            }
            System.out.println("─".repeat(60));
            
        } catch (Exception e) {
            exceptionHandler.handleException(e, "xem danh sách dự án");
        }
    }
    
    /**
     * View project details
     */
    private void viewProjectDetails() {
        try {
            Long projectId = inputHelper.getLongInput("Nhập ID dự án: ");
            ProjectResponse project = projectService.getById(projectId);
            
            // Display project details
            System.out.println("\n" + "─".repeat(60));
            System.out.println("📁 DỰ ÁN: " + project.getName());
            System.out.println("─".repeat(60));
            System.out.println("ID: " + project.getId());
            System.out.println("Mô tả: " + (project.getDescription() != null ? project.getDescription() : "Không có"));
            System.out.println("Ngày bắt đầu: " + project.getStartDate());
            System.out.println("Ngày tạo: " + project.getCreatedAt());
            System.out.println("─".repeat(60));
            
        } catch (Exception e) {
            exceptionHandler.handleException(e, "xem chi tiết dự án");
        }
    }
    
    /**
     * Update a project
     */
    private void updateProject() {
        try {
            Long projectId = inputHelper.getLongInput("Nhập ID dự án cần cập nhật: ");
            ProjectResponse existingProject = projectService.getById(projectId);
            
            System.out.println("--- CẬP NHẬT DỰ ÁN: " + existingProject.getName() + " ---");
            
            UpdateProjectRequest request = new UpdateProjectRequest();
            request.setName(inputHelper.getOptionalStringInput("Tên mới (Enter để giữ nguyên): "));
            request.setDescription(inputHelper.getOptionalStringInput("Mô tả mới (Enter để giữ nguyên): "));
            request.setStartDate(inputHelper.getOptionalDateInput("Ngày bắt đầu mới"));
            
            // Only set non-empty values
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                request.setName(existingProject.getName());
            }
            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                request.setDescription(existingProject.getDescription());
            }
            if (request.getStartDate() == null) {
                request.setStartDate(existingProject.getStartDate());
            }
            
            projectService.update(projectId, request);
            displayHelper.displaySuccess("Cập nhật dự án thành công!");
            
        } catch (Exception e) {
            exceptionHandler.handleException(e, "cập nhật dự án");
        }
    }
    
    /**
     * Delete a project
     */
    private void deleteProject() {
        try {
            Long projectId = inputHelper.getLongInput("Nhập ID dự án cần xóa: ");
            ProjectResponse project = projectService.getById(projectId);
            
            System.out.println("--- XÁC NHẬN XÓA DỰ ÁN ---");
            System.out.println("Dự án: " + project.getName());
            System.out.println("⚠️ Hành động này không thể hoàn tác!");
            
            if (inputHelper.getYesNoInput("Bạn có chắc chắn muốn xóa? (y/n): ")) {
                projectService.delete(projectId);
                displayHelper.displaySuccess("Đã xóa dự án thành công!");
            } else {
                System.out.println("Đã hủy thao tác xóa.");
            }
            
        } catch (Exception e) {
            exceptionHandler.handleException(e, "xóa dự án");
        }
    }
    
    /**
     * Add member to project
     */
    private void addMemberToProject() {
        Long projectId = inputHelper.getLongInput("Nhập ID dự án: ");
        addMemberToProject(projectId);
    }
    
    /**
     * Add member to project with given project ID
     */
    private void addMemberToProject(Long projectId) {
        try {
            // Display available users
            List<UserResponse> users = userService.getAll();
            if (users.isEmpty()) {
                System.out.println("❌ Chưa có người dùng nào trong hệ thống.");
                return;
            }
            
            System.out.println("\n👥 DANH SÁCH NGƯỜI DÙNG");
            System.out.println("─".repeat(50));
            System.out.printf("%-5s %-25s %-20s%n", "ID", "Tên", "Email");
            System.out.println("─".repeat(50));
            
            for (UserResponse user : users) {
                System.out.printf("%-5d %-25s %-20s%n",
                    user.getId(),
                    truncate(user.getFullName(), 25),
                    truncate(user.getEmail(), 20)
                );
            }
            System.out.println("─".repeat(50));
            
            Long userId = inputHelper.getLongInput("Chọn ID người dùng: ");
            projectService.addMember(projectId, userId);
            displayHelper.displaySuccess("Đã thêm thành viên vào dự án!");
            
        } catch (Exception e) {
            exceptionHandler.handleException(e, "thêm thành viên");
        }
    }
    
    /**
     * Remove member from project
     */
    private void removeMemberFromProject() {
        try {
            Long projectId = inputHelper.getLongInput("Nhập ID dự án: ");
            Long userId = inputHelper.getLongInput("Nhập ID người dùng cần xóa: ");
            
            projectService.removeMember(projectId, userId);
            displayHelper.displaySuccess("Đã xóa thành viên khỏi dự án!");
            
        } catch (Exception e) {
            exceptionHandler.handleException(e, "xóa thành viên");
        }
    }
    
    /**
     * View tasks of a project
     */
    private void viewProjectTasks() {
        try {
            Long projectId = inputHelper.getLongInput("Nhập ID dự án: ");
            ProjectResponse project = projectService.getById(projectId);
            
            System.out.println("\n📋 CÔNG VIỆC CỦA DỰ ÁN: " + project.getName());
            // This would require a method to get tasks by project ID
            // For now, we'll just show a message
            System.out.println("🔄 Tính năng này đang được phát triển...");
            
        } catch (Exception e) {
            exceptionHandler.handleException(e, "xem công việc dự án");
        }
    }
    
    /**
     * Truncate string for display
     */
    private String truncate(String str, int length) {
        if (str == null) return "N/A";
        return str.length() <= length ? str : str.substring(0, length - 3) + "...";
    }
}
