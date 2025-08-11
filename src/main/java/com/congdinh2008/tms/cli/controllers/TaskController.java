package com.congdinh2008.tms.cli.controllers;

import com.congdinh2008.tms.cli.helpers.InputHelper;
import com.congdinh2008.tms.cli.helpers.DisplayHelper;
import com.congdinh2008.tms.cli.exception.CLIExceptionHandler;
import com.congdinh2008.tms.services.*;
import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;
import com.congdinh2008.tms.dto.request.CreateTaskRequest;
import com.congdinh2008.tms.dto.request.UpdateTaskRequest;
import com.congdinh2008.tms.dto.response.TaskResponse;
import com.congdinh2008.tms.dto.response.ProjectResponse;
import com.congdinh2008.tms.dto.response.UserResponse;
import com.congdinh2008.tms.dto.response.TagResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CLI Controller for Task Management
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Component
public class TaskController {
    
    private final TaskService taskService;
    private final ProjectService projectService;
    private final UserService userService;
    private final TagService tagService;
    private final InputHelper inputHelper;
    private final DisplayHelper displayHelper;
    private final CLIExceptionHandler exceptionHandler;
    
    @Autowired
    public TaskController(TaskService taskService,
                         ProjectService projectService,
                         UserService userService,
                         TagService tagService,
                         InputHelper inputHelper,
                         DisplayHelper displayHelper,
                         CLIExceptionHandler exceptionHandler) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.userService = userService;
        this.tagService = tagService;
        this.inputHelper = inputHelper;
        this.displayHelper = displayHelper;
        this.exceptionHandler = exceptionHandler;
    }
    
    public void handleTaskMenu() {
        while (true) {
            displayTaskMenu();
            int choice = inputHelper.getIntInput("Chọn chức năng: ");
            
            try {
                switch (choice) {
                    case 1 -> createTask();
                    case 2 -> viewAllTasks();
                    case 3 -> viewTaskById();
                    case 4 -> updateTask();
                    case 5 -> deleteTask();
                    case 6 -> assignTask();
                    case 7 -> changeTaskStatus();
                    case 0 -> { return; }
                    default -> System.out.println("❌ Lựa chọn không hợp lệ!");
                }
            } catch (Exception e) {
                exceptionHandler.handleException(e);
            }
        }
    }
    
    private void displayTaskMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📋 QUẢN LÝ CÔNG VIỆC");
        System.out.println("=".repeat(50));
        System.out.println("1. ➕ Tạo công việc mới");
        System.out.println("2. 📋 Xem tất cả công việc");
        System.out.println("3. 🔍 Xem chi tiết công việc");
        System.out.println("4. ✏️ Cập nhật công việc");
        System.out.println("5. 🗑️ Xóa công việc");
        System.out.println("6. 👤 Gán công việc cho người dùng");
        System.out.println("7. 🔄 Thay đổi trạng thái công việc");
        System.out.println("0. ⬅️ Quay lại");
        System.out.println("=".repeat(50));
    }
    
    private void createTask() {
        System.out.println("\n➕ TẠO CÔNG VIỆC MỚI");
        System.out.println("=".repeat(30));
        
        // Select project
        List<ProjectResponse> projects = projectService.getAll();
        if (projects.isEmpty()) {
            System.out.println("❌ Không có dự án nào. Vui lòng tạo dự án trước!");
            return;
        }
        
        System.out.println("📁 Danh sách dự án:");
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getName());
        }
        
        int projectIndex = inputHelper.getIntInput("Chọn dự án (số thứ tự): ") - 1;
        if (projectIndex < 0 || projectIndex >= projects.size()) {
            System.out.println("❌ Lựa chọn không hợp lệ!");
            return;
        }
        
        ProjectResponse selectedProject = projects.get(projectIndex);
        
        // Get task details
        String title = inputHelper.getStringInput("Tiêu đề công việc: ");
        String description = inputHelper.getStringInput("Mô tả: ");
        TaskPriority priority = inputHelper.getEnumInput("Độ ưu tiên", TaskPriority.class);
        LocalDateTime dueDate = inputHelper.getDateTimeInput("Hạn hoàn thành (dd/MM/yyyy HH:mm): ");
        
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setPriority(priority);
        request.setDueDate(dueDate != null ? dueDate.toLocalDate() : null);
        request.setProjectId(selectedProject.getId());
        
        TaskResponse task = taskService.create(request);
        System.out.println("✅ Tạo công việc thành công!");
        displayHelper.displayTaskResponse(task);
    }
    
    private void viewAllTasks() {
        System.out.println("\n📋 DANH SÁCH TẤT CẢ CÔNG VIỆC");
        System.out.println("=".repeat(40));
        
        List<TaskResponse> tasks = taskService.getAll();
        if (tasks.isEmpty()) {
            System.out.println("📭 Không có công việc nào.");
            return;
        }
        
        tasks.forEach(displayHelper::displayTaskResponse);
    }
    
    private void viewTaskById() {
        System.out.println("\n🔍 XEM CHI TIẾT CÔNG VIỆC");
        System.out.println("=".repeat(30));
        
        Long taskId = inputHelper.getLongInput("Nhập ID công việc: ");
        try {
            TaskResponse task = taskService.getById(taskId);
            displayHelper.displayTaskResponse(task);
        } catch (Exception e) {
            System.out.println("❌ Không tìm thấy công việc với ID: " + taskId);
        }
    }
    
    private void updateTask() {
        System.out.println("\n✏️ CẬP NHẬT CÔNG VIỆC");
        System.out.println("=".repeat(30));
        
        Long taskId = inputHelper.getLongInput("Nhập ID công việc cần cập nhật: ");
        try {
            TaskResponse existingTask = taskService.getById(taskId);
            
            System.out.println("📋 Thông tin hiện tại:");
            displayHelper.displayTaskResponse(existingTask);
            
            System.out.println("\n📝 Nhập thông tin mới (Enter để giữ nguyên):");
            
            String title = inputHelper.getStringInput("Tiêu đề [" + existingTask.getTitle() + "]: ");
            if (title.trim().isEmpty()) title = existingTask.getTitle();
            
            String description = inputHelper.getStringInput("Mô tả [" + existingTask.getDescription() + "]: ");
            if (description.trim().isEmpty()) description = existingTask.getDescription();
            
            System.out.println("Độ ưu tiên hiện tại: " + existingTask.getPriority());
            TaskPriority priority = inputHelper.getEnumInput("Độ ưu tiên mới", TaskPriority.class);
            
            LocalDateTime dueDate = inputHelper.getDateTimeInput("Hạn hoàn thành mới (dd/MM/yyyy HH:mm): ");
            
            UpdateTaskRequest request = new UpdateTaskRequest();
            request.setTitle(title);
            request.setDescription(description);
            request.setPriority(priority);
            request.setDueDate(dueDate != null ? dueDate.toLocalDate() : null);
            
            TaskResponse updatedTask = taskService.update(taskId, request);
            System.out.println("✅ Cập nhật công việc thành công!");
            displayHelper.displayTaskResponse(updatedTask);
        } catch (Exception e) {
            System.out.println("❌ Không tìm thấy công việc với ID: " + taskId);
        }
    }
    
    private void deleteTask() {
        System.out.println("\n🗑️ XÓA CÔNG VIỆC");
        System.out.println("=".repeat(20));
        
        Long taskId = inputHelper.getLongInput("Nhập ID công việc cần xóa: ");
        try {
            TaskResponse task = taskService.getById(taskId);
            
            System.out.println("📋 Thông tin công việc sẽ bị xóa:");
            displayHelper.displayTaskResponse(task);
            
            boolean confirm = inputHelper.getYesNoInput("Bạn có chắc chắn muốn xóa? (y/n): ");
            if (confirm) {
                taskService.delete(taskId);
                System.out.println("✅ Đã xóa công việc thành công!");
            } else {
                System.out.println("❌ Đã hủy thao tác xóa.");
            }
        } catch (Exception e) {
            System.out.println("❌ Không tìm thấy công việc với ID: " + taskId);
        }
    }
    
    private void assignTask() {
        System.out.println("\n👤 GÁN CÔNG VIỆC CHO NGƯỜI DÙNG");
        System.out.println("=".repeat(35));
        
        Long taskId = inputHelper.getLongInput("Nhập ID công việc: ");
        try {
            TaskResponse task = taskService.getById(taskId);
            
            List<UserResponse> users = userService.getAll();
            if (users.isEmpty()) {
                System.out.println("❌ Không có người dùng nào trong hệ thống!");
                return;
            }
            
            System.out.println("👥 Danh sách người dùng:");
            for (int i = 0; i < users.size(); i++) {
                System.out.println((i + 1) + ". " + users.get(i).getFullName() + " (" + users.get(i).getEmail() + ")");
            }
            
            int userIndex = inputHelper.getIntInput("Chọn người dùng (số thứ tự): ") - 1;
            if (userIndex < 0 || userIndex >= users.size()) {
                System.out.println("❌ Lựa chọn không hợp lệ!");
                return;
            }
            
            UserResponse selectedUser = users.get(userIndex);
            taskService.assignTask(taskId, selectedUser.getId());
            System.out.println("✅ Đã gán công việc cho " + selectedUser.getFullName() + " thành công!");
        } catch (Exception e) {
            System.out.println("❌ Không tìm thấy công việc với ID: " + taskId);
        }
    }
    
    private void changeTaskStatus() {
        System.out.println("\n🔄 THAY ĐỔI TRẠNG THÁI CÔNG VIỆC");
        System.out.println("=".repeat(35));
        
        Long taskId = inputHelper.getLongInput("Nhập ID công việc: ");
        try {
            TaskResponse task = taskService.getById(taskId);
            
            System.out.println("📋 Công việc: " + task.getTitle());
            System.out.println("🔸 Trạng thái hiện tại: " + task.getStatus());
            
            TaskStatus newStatus = inputHelper.getEnumInput("Trạng thái mới", TaskStatus.class);
            taskService.changeStatus(taskId, newStatus);
            System.out.println("✅ Đã cập nhật trạng thái thành công!");
        } catch (Exception e) {
            System.out.println("❌ Không tìm thấy công việc với ID: " + taskId);
        }
    }
}
