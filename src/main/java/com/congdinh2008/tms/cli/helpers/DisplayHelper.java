package com.congdinh2008.tms.cli.helpers;

import com.congdinh2008.tms.entities.*;
import com.congdinh2008.tms.dto.response.*;
import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.enums.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Helper class for displaying formatted output in CLI
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Component
public class DisplayHelper {
    
    private static final String SEPARATOR = "─".repeat(60);
    private static final String NO_INFO = "Không có";
    private static final String NOT_SET = "Chưa xác định";
    private static final String DESCRIPTION_LABEL = "Mô tả: ";
    private static final String CREATED_AT_LABEL = "Ngày tạo: ";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Display a single project with details
     */
    public void displayProject(Project project) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("📁 DỰ ÁN: " + project.getName());
        System.out.println(SEPARATOR);
        System.out.println("ID: " + project.getId());
        System.out.println(DESCRIPTION_LABEL + (project.getDescription() != null ? project.getDescription() : NO_INFO));
        System.out.println("Ngày bắt đầu: " + (project.getStartDate() != null ? project.getStartDate().format(DATE_FORMATTER) : NOT_SET));
        System.out.println("Số thành viên: " + (project.getMembers() != null ? project.getMembers().size() : 0));
        System.out.println("Số công việc: " + (project.getTasks() != null ? project.getTasks().size() : 0));
        System.out.println(CREATED_AT_LABEL + project.getCreatedAt().format(DATETIME_FORMATTER));
        System.out.println(SEPARATOR);
    }
    
    /**
     * Display a list of projects in table format
     */
    public void displayProjectList(List<Project> projects) {
        if (projects.isEmpty()) {
            System.out.println("📭 Không có dự án nào.");
            return;
        }
        
        System.out.println("\n📁 DANH SÁCH DỰ ÁN");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-30s %-15s %-10s%n", "ID", "Tên dự án", "Ngày bắt đầu", "Thành viên");
        System.out.println(SEPARATOR);
        
        for (Project project : projects) {
            System.out.printf("%-5d %-30s %-15s %-10d%n",
                project.getId(),
                truncate(project.getName(), 30),
                project.getStartDate() != null ? project.getStartDate().format(DATE_FORMATTER) : "N/A",
                project.getMembers() != null ? project.getMembers().size() : 0
            );
        }
        System.out.println(SEPARATOR);
    }
    
    /**
     * Display a single task with details
     */
    public void displayTask(Task task) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("📋 CÔNG VIỆC: " + task.getTitle());
        System.out.println(SEPARATOR);
        System.out.println("ID: " + task.getId());
        System.out.println(DESCRIPTION_LABEL + (task.getDescription() != null ? task.getDescription() : NO_INFO));
        System.out.println("Trạng thái: " + getStatusEmoji(task.getStatus()) + " " + task.getStatus());
        System.out.println("Độ ưu tiên: " + getPriorityEmoji(task.getPriority()) + " " + task.getPriority());
        System.out.println("Hạn hoàn thành: " + (task.getDueDate() != null ? task.getDueDate().format(DATETIME_FORMATTER) : NOT_SET));
        System.out.println("Dự án: " + (task.getProject() != null ? task.getProject().getName() : "N/A"));
        System.out.println("Người thực hiện: " + (task.getAssignee() != null ? task.getAssignee().getName() : "Chưa phân công"));
        if (task.getParentTask() != null) {
            System.out.println("Công việc cha: " + task.getParentTask().getTitle());
        }
        if (task.getTags() != null && !task.getTags().isEmpty()) {
            System.out.print("Tags: ");
            task.getTags().forEach(tag -> System.out.print("#" + tag.getName() + " "));
            System.out.println();
        }
        System.out.println(CREATED_AT_LABEL + task.getCreatedAt().format(DATETIME_FORMATTER));
        System.out.println(SEPARATOR);
    }
    
    /**
     * Display a list of tasks in table format
     */
    public void displayTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("📭 Không có công việc nào.");
            return;
        }
        
        System.out.println("\n📋 DANH SÁCH CÔNG VIỆC");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-25s %-12s %-10s %-20s %-15s%n", 
            "ID", "Tiêu đề", "Trạng thái", "Ưu tiên", "Người thực hiện", "Hạn hoàn thành");
        System.out.println(SEPARATOR);
        
        for (Task task : tasks) {
            System.out.printf("%-5d %-25s %-12s %-10s %-20s %-15s%n",
                task.getId(),
                truncate(task.getTitle(), 25),
                getStatusEmoji(task.getStatus()) + task.getStatus().toString().substring(0, Math.min(8, task.getStatus().toString().length())),
                getPriorityEmoji(task.getPriority()) + task.getPriority().toString().substring(0, Math.min(6, task.getPriority().toString().length())),
                task.getAssignee() != null ? truncate(task.getAssignee().getName(), 20) : "Chưa phân công",
                task.getDueDate() != null ? task.getDueDate().format(DATE_FORMATTER) : "N/A"
            );
        }
        System.out.println(SEPARATOR);
    }
    
    /**
     * Display a single user with details
     */
    public void displayUser(User user) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("👤 NGƯỜI DÙNG: " + user.getName());
        System.out.println(SEPARATOR);
        System.out.println("ID: " + user.getId());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Số công việc được gán: " + (user.getAssignedTasks() != null ? user.getAssignedTasks().size() : 0));
        System.out.println("Số dự án tham gia: " + (user.getProjects() != null ? user.getProjects().size() : 0));
        System.out.println(CREATED_AT_LABEL + user.getCreatedAt().format(DATETIME_FORMATTER));
        System.out.println(SEPARATOR);
    }
    
    /**
     * Display a list of users in table format
     */
    public void displayUserList(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("📭 Không có người dùng nào.");
            return;
        }
        
        System.out.println("\n👥 DANH SÁCH NGƯỜI DÙNG");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-25s %-30s %-15s%n", 
            "ID", "Tên", "Email", "Công việc");
        System.out.println(SEPARATOR);
        
        for (User user : users) {
            System.out.printf("%-5d %-25s %-30s %-15d%n",
                user.getId(),
                truncate(user.getName(), 25),
                truncate(user.getEmail(), 30),
                user.getAssignedTasks() != null ? user.getAssignedTasks().size() : 0
            );
        }
        System.out.println(SEPARATOR);
    }
    
    /**
     * Display a single tag with details
     */
    public void displayTag(Tag tag) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("🏷️ TAG: " + tag.getName());
        System.out.println(SEPARATOR);
        System.out.println("ID: " + tag.getId());
        System.out.println("Số công việc sử dụng: " + (tag.getTasks() != null ? tag.getTasks().size() : 0));
        System.out.println(CREATED_AT_LABEL + tag.getCreatedAt().format(DATETIME_FORMATTER));
        System.out.println(SEPARATOR);
    }
    
    /**
     * Display a list of tags in table format
     */
    public void displayTagList(List<Tag> tags) {
        if (tags.isEmpty()) {
            System.out.println("📭 Không có tag nào.");
            return;
        }
        
        System.out.println("\n🏷️ DANH SÁCH TAGS");
        System.out.println(SEPARATOR);
        System.out.printf("%-5s %-30s %-10s%n", 
            "ID", "Tên", "Sử dụng");
        System.out.println(SEPARATOR);
        
        for (Tag tag : tags) {
            System.out.printf("%-5d %-30s %-10d%n",
                tag.getId(),
                truncate(tag.getName(), 30),
                tag.getTasks() != null ? tag.getTasks().size() : 0
            );
        }
        System.out.println(SEPARATOR);
    }
    
    /**
     * Display success message with icon
     */
    public void displaySuccess(String message) {
        System.out.println("✅ " + message);
    }
    
    /**
     * Display error message with icon
     */
    public void displayError(String message) {
        System.out.println("❌ " + message);
    }
    
    /**
     * Display warning message with icon
     */
    public void displayWarning(String message) {
        System.out.println("⚠️ " + message);
    }
    
    /**
     * Display info message with icon
     */
    public void displayInfo(String message) {
        System.out.println("ℹ️ " + message);
    }
    
    /**
     * Get status emoji for task status
     */
    private String getStatusEmoji(TaskStatus status) {
        return switch (status) {
            case TODO -> "📝";
            case IN_PROGRESS -> "⚡";
            case DONE -> "✅";
        };
    }
    
    /**
     * Get priority emoji for task priority
     */
    private String getPriorityEmoji(TaskPriority priority) {
        return switch (priority) {
            case HIGH -> "🔴";
            case MEDIUM -> "🟡";
            case LOW -> "🟢";
        };
    }
    
    /**
     * Display a TaskResponse with details
     */
    public void displayTaskResponse(TaskResponse task) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("📋 CÔNG VIỆC: " + task.getTitle());
        System.out.println(SEPARATOR);
        System.out.println("ID: " + task.getId());
        System.out.println(DESCRIPTION_LABEL + (task.getDescription() != null ? task.getDescription() : NO_INFO));
        System.out.println("Trạng thái: " + getStatusEmoji(task.getStatus()) + " " + task.getStatus());
        System.out.println("Độ ưu tiên: " + getPriorityEmoji(task.getPriority()) + " " + task.getPriority());
        System.out.println("Hạn hoàn thành: " + (task.getDueDate() != null ? task.getDueDate().format(DATE_FORMATTER) : NOT_SET));
        System.out.println("Dự án: " + (task.getProject() != null ? task.getProject().getName() : "N/A"));
        System.out.println("Người thực hiện: " + (task.getAssignee() != null ? task.getAssignee().getFullName() : "Chưa phân công"));
        if (task.getParentTask() != null) {
            System.out.println("Công việc cha: " + task.getParentTask().getTitle());
        }
        if (task.getTags() != null && !task.getTags().isEmpty()) {
            System.out.print("Tags: ");
            task.getTags().forEach(tag -> System.out.print("#" + tag.getName() + " "));
            System.out.println();
        }
        System.out.println(CREATED_AT_LABEL + task.getCreatedAt().format(DATETIME_FORMATTER));
        System.out.println(SEPARATOR);
    }
    
    /**
     * Display a ProjectResponse with details
     */
    public void displayProjectResponse(ProjectResponse project) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("📁 DỰ ÁN: " + project.getName());
        System.out.println(SEPARATOR);
        System.out.println("ID: " + project.getId());
        System.out.println(DESCRIPTION_LABEL + (project.getDescription() != null ? project.getDescription() : NO_INFO));
        System.out.println("Ngày bắt đầu: " + (project.getStartDate() != null ? project.getStartDate().format(DATE_FORMATTER) : NOT_SET));
        System.out.println("Số thành viên: " + (project.getMembers() != null ? project.getMembers().size() : 0));
        System.out.println("Số công việc: " + (project.getTaskCount() != null ? project.getTaskCount() : 0));
        System.out.println(CREATED_AT_LABEL + project.getCreatedAt().format(DATETIME_FORMATTER));
        System.out.println(SEPARATOR);
    }
    
    /**
     * Display a UserResponse with details
     */
    public void displayUserResponse(UserResponse user) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("👤 NGƯỜI DÙNG: " + user.getFullName());
        System.out.println(SEPARATOR);
        System.out.println("ID: " + user.getId());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Tên đăng nhập: " + user.getUsername());
        System.out.println("Trạng thái: " + (user.getIsActive() != null && user.getIsActive() ? "✅ Hoạt động" : "❌ Không hoạt động"));
        System.out.println(CREATED_AT_LABEL + user.getCreatedAt().format(DATETIME_FORMATTER));
        System.out.println(SEPARATOR);
    }
    
    /**
     * Display a TagResponse with details
     */
    public void displayTagResponse(TagResponse tag) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("🏷️ TAG: " + tag.getName());
        System.out.println(SEPARATOR);
        System.out.println("ID: " + tag.getId());
        System.out.println("Màu sắc: " + tag.getColor());
        System.out.println(DESCRIPTION_LABEL + (tag.getDescription() != null ? tag.getDescription() : NO_INFO));
        System.out.println(CREATED_AT_LABEL + tag.getCreatedAt().format(DATETIME_FORMATTER));
        System.out.println(SEPARATOR);
    }
    
    /**
     * Truncate string to specified length
     */
    private String truncate(String str, int length) {
        if (str == null) return "N/A";
        return str.length() <= length ? str : str.substring(0, length - 3) + "...";
    }
}
