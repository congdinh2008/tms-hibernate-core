package com.congdinh2008.tms.cli;

import com.congdinh2008.tms.dto.request.*;
import com.congdinh2008.tms.dto.response.*;
import com.congdinh2008.tms.enums.TaskPriority;
import com.congdinh2008.tms.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Service for loading sample data for demonstration
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Service
public class SampleDataService {
    
    private static final Logger log = LoggerFactory.getLogger(SampleDataService.class);
    
    private final UserService userService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final TagService tagService;
    
    @Autowired
    public SampleDataService(UserService userService, 
                           ProjectService projectService,
                           TaskService taskService,
                           TagService tagService) {
        this.userService = userService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.tagService = tagService;
    }
    
    /**
     * Load sample data for demonstration
     */
    public void loadSampleData() {
        try {
            System.out.println("🔄 Đang tải dữ liệu mẫu...");
            
            // Create sample data
            List<UserResponse> users = createSampleUsers();
            List<TagResponse> tags = createSampleTags();
            List<ProjectResponse> projects = createSampleProjects(users);
            createSampleTasks(projects, users);
            
            System.out.println("✅ Đã tải xong dữ liệu mẫu!");
            System.out.println("📊 Tổng quan:");
            System.out.println("   - Người dùng: " + users.size());
            System.out.println("   - Dự án: " + projects.size());
            System.out.println("   - Tags: " + tags.size());
            System.out.println("   - Công việc: " + taskService.getAll().size());
            
        } catch (Exception e) {
            log.error("Error loading sample data", e);
            System.out.println("❌ Lỗi khi tải dữ liệu mẫu: " + e.getMessage());
        }
    }
    
    /**
     * Create sample users
     */
    private List<UserResponse> createSampleUsers() {
        System.out.println("👥 Tạo người dùng mẫu...");
        
        try {
            CreateUserRequest adminRequest = new CreateUserRequest();
            adminRequest.setUsername("admin");
            adminRequest.setEmail("admin@tms.com");
            adminRequest.setFullName("Nguyễn Văn Admin");
            adminRequest.setPassword("admin123");
            UserResponse admin = userService.create(adminRequest);
            
            CreateUserRequest managerRequest = new CreateUserRequest();
            managerRequest.setUsername("manager");
            managerRequest.setEmail("manager@tms.com");
            managerRequest.setFullName("Trần Thị Manager");
            managerRequest.setPassword("manager123");
            UserResponse manager = userService.create(managerRequest);
            
            CreateUserRequest dev1Request = new CreateUserRequest();
            dev1Request.setUsername("developer1");
            dev1Request.setEmail("dev1@tms.com");
            dev1Request.setFullName("Lê Văn Developer");
            dev1Request.setPassword("dev123");
            UserResponse developer1 = userService.create(dev1Request);
            
            CreateUserRequest dev2Request = new CreateUserRequest();
            dev2Request.setUsername("developer2");
            dev2Request.setEmail("dev2@tms.com");
            dev2Request.setFullName("Phạm Thị Coder");
            dev2Request.setPassword("dev123");
            UserResponse developer2 = userService.create(dev2Request);
            
            CreateUserRequest testerRequest = new CreateUserRequest();
            testerRequest.setUsername("tester");
            testerRequest.setEmail("tester@tms.com");
            testerRequest.setFullName("Hoàng Văn Tester");
            testerRequest.setPassword("test123");
            UserResponse tester = userService.create(testerRequest);
            
            return Arrays.asList(admin, manager, developer1, developer2, tester);
        } catch (Exception e) {
            log.warn("Some users may already exist, continuing...", e);
            // Return existing users
            return userService.getAll();
        }
    }
    
    /**
     * Create sample tags
     */
    private List<TagResponse> createSampleTags() {
        System.out.println("🏷️ Tạo tags mẫu...");
        
        try {
            CreateTagRequest bugRequest = new CreateTagRequest();
            bugRequest.setName("bug");
            TagResponse bugTag = tagService.create(bugRequest);
            
            CreateTagRequest featureRequest = new CreateTagRequest();
            featureRequest.setName("feature");
            TagResponse featureTag = tagService.create(featureRequest);
            
            CreateTagRequest urgentRequest = new CreateTagRequest();
            urgentRequest.setName("urgent");
            TagResponse urgentTag = tagService.create(urgentRequest);
            
            CreateTagRequest backendRequest = new CreateTagRequest();
            backendRequest.setName("backend");
            TagResponse backendTag = tagService.create(backendRequest);
            
            CreateTagRequest frontendRequest = new CreateTagRequest();
            frontendRequest.setName("frontend");
            TagResponse frontendTag = tagService.create(frontendRequest);
            
            CreateTagRequest testingRequest = new CreateTagRequest();
            testingRequest.setName("testing");
            TagResponse testingTag = tagService.create(testingRequest);
            
            return Arrays.asList(bugTag, featureTag, urgentTag, backendTag, frontendTag, testingTag);
        } catch (Exception e) {
            log.warn("Some tags may already exist, continuing...", e);
            return tagService.getAll();
        }
    }
    
    /**
     * Create sample projects
     */
    private List<ProjectResponse> createSampleProjects(List<UserResponse> users) {
        System.out.println("📁 Tạo dự án mẫu...");
        
        try {
            CreateProjectRequest ecommerceRequest = new CreateProjectRequest();
            ecommerceRequest.setName("E-commerce Platform");
            ecommerceRequest.setDescription("Phát triển nền tảng thương mại điện tử với đầy đủ tính năng");
            ecommerceRequest.setStartDate(LocalDate.now().minusMonths(2));
            ProjectResponse ecommerceProject = projectService.create(ecommerceRequest);
            
            CreateProjectRequest mobileRequest = new CreateProjectRequest();
            mobileRequest.setName("Mobile Task Manager");
            mobileRequest.setDescription("Ứng dụng quản lý công việc trên di động");
            mobileRequest.setStartDate(LocalDate.now().minusMonths(1));
            ProjectResponse mobileApp = projectService.create(mobileRequest);
            
            CreateProjectRequest apiRequest = new CreateProjectRequest();
            apiRequest.setName("API Gateway Service");
            apiRequest.setDescription("Service gateway cho microservices architecture");
            apiRequest.setStartDate(LocalDate.now().minusWeeks(3));
            ProjectResponse apiGateway = projectService.create(apiRequest);
            
            // Add members to projects
            if (!users.isEmpty()) {
                try {
                    // Add first 3 users to ecommerce project
                    for (int i = 0; i < Math.min(3, users.size()); i++) {
                        projectService.addMember(ecommerceProject.getId(), users.get(i).getId());
                    }
                    
                    // Add different users to mobile app
                    for (int i = 1; i < Math.min(4, users.size()); i++) {
                        projectService.addMember(mobileApp.getId(), users.get(i).getId());
                    }
                    
                    // Add users to API gateway
                    for (int i = 2; i < users.size(); i++) {
                        projectService.addMember(apiGateway.getId(), users.get(i).getId());
                    }
                } catch (Exception e) {
                    log.warn("Could not add all members to projects", e);
                }
            }
            
            return Arrays.asList(ecommerceProject, mobileApp, apiGateway);
        } catch (Exception e) {
            log.warn("Some projects may already exist, continuing...", e);
            return projectService.getAll();
        }
    }
    
    /**
     * Create sample tasks
     */
    private void createSampleTasks(List<ProjectResponse> projects, List<UserResponse> users) {
        System.out.println("📋 Tạo công việc mẫu...");
        
        if (projects.isEmpty() || users.isEmpty()) {
            System.out.println("⚠️ Không có dự án hoặc người dùng để tạo công việc");
            return;
        }
        
        try {
            // Tasks for E-commerce project
            if (!projects.isEmpty()) {
                createTasksForEcommerceProject(projects.get(0), users);
            }
            
            // Tasks for Mobile app project
            if (projects.size() > 1) {
                createTasksForMobileProject(projects.get(1), users);
            }
            
            // Tasks for API Gateway project
            if (projects.size() > 2) {
                createTasksForApiProject(projects.get(2), users);
            }
            
        } catch (Exception e) {
            log.error("Error creating sample tasks", e);
            System.out.println("❌ Lỗi khi tạo công việc mẫu: " + e.getMessage());
        }
    }
    
    private void createTasksForEcommerceProject(ProjectResponse project, List<UserResponse> users) {
        try {
            // Main task
            CreateTaskRequest userManagementRequest = new CreateTaskRequest();
            userManagementRequest.setTitle("User Management System");
            userManagementRequest.setDescription("Phát triển hệ thống quản lý người dùng");
            userManagementRequest.setPriority(TaskPriority.HIGH);
            userManagementRequest.setDueDate(LocalDate.now().plusWeeks(4));
            userManagementRequest.setProjectId(project.getId());
            if (!users.isEmpty()) {
                userManagementRequest.setAssigneeId(users.get(0).getId());
            }
            TaskResponse userManagement = taskService.create(userManagementRequest);
            
            // Sub tasks
            CreateTaskRequest subTask1 = new CreateTaskRequest();
            subTask1.setTitle("Design User Database Schema");
            subTask1.setDescription("Thiết kế cơ sở dữ liệu cho người dùng");
            subTask1.setPriority(TaskPriority.HIGH);
            subTask1.setDueDate(LocalDate.now().plusWeeks(1));
            subTask1.setProjectId(project.getId());
            subTask1.setParentTaskId(userManagement.getId());
            if (users.size() > 1) {
                subTask1.setAssigneeId(users.get(1).getId());
            }
            taskService.create(subTask1);
            
            CreateTaskRequest subTask2 = new CreateTaskRequest();
            subTask2.setTitle("Implement User Authentication");
            subTask2.setDescription("Triển khai xác thực người dùng");
            subTask2.setPriority(TaskPriority.HIGH);
            subTask2.setDueDate(LocalDate.now().plusWeeks(2));
            subTask2.setProjectId(project.getId());
            subTask2.setParentTaskId(userManagement.getId());
            if (users.size() > 2) {
                subTask2.setAssigneeId(users.get(2).getId());
            }
            taskService.create(subTask2);
            
            // Product management task
            CreateTaskRequest productTask = new CreateTaskRequest();
            productTask.setTitle("Product Catalog Development");
            productTask.setDescription("Phát triển catalog sản phẩm");
            productTask.setPriority(TaskPriority.MEDIUM);
            productTask.setDueDate(LocalDate.now().plusWeeks(6));
            productTask.setProjectId(project.getId());
            if (users.size() > 1) {
                productTask.setAssigneeId(users.get(1).getId());
            }
            taskService.create(productTask);
            
            // Payment integration
            CreateTaskRequest paymentTask = new CreateTaskRequest();
            paymentTask.setTitle("Payment Gateway Integration");
            paymentTask.setDescription("Tích hợp cổng thanh toán");
            paymentTask.setPriority(TaskPriority.HIGH);
            paymentTask.setDueDate(LocalDate.now().plusWeeks(8));
            paymentTask.setProjectId(project.getId());
            taskService.create(paymentTask);
        } catch (Exception e) {
            log.warn("Error creating tasks for ecommerce project", e);
        }
    }
    
    private void createTasksForMobileProject(ProjectResponse project, List<UserResponse> users) {
        try {
            CreateTaskRequest uiTask = new CreateTaskRequest();
            uiTask.setTitle("Mobile UI/UX Design");
            uiTask.setDescription("Thiết kế giao diện người dùng cho ứng dụng mobile");
            uiTask.setPriority(TaskPriority.HIGH);
            uiTask.setDueDate(LocalDate.now().plusWeeks(3));
            uiTask.setProjectId(project.getId());
            if (users.size() > 1) {
                uiTask.setAssigneeId(users.get(1).getId());
            }
            taskService.create(uiTask);
            
            CreateTaskRequest listTask = new CreateTaskRequest();
            listTask.setTitle("Task List Component");
            listTask.setDescription("Phát triển component danh sách công việc");
            listTask.setPriority(TaskPriority.MEDIUM);
            listTask.setDueDate(LocalDate.now().plusWeeks(2));
            listTask.setProjectId(project.getId());
            if (users.size() > 2) {
                listTask.setAssigneeId(users.get(2).getId());
            }
            taskService.create(listTask);
            
            CreateTaskRequest notificationTask = new CreateTaskRequest();
            notificationTask.setTitle("Push Notification");
            notificationTask.setDescription("Triển khai thông báo đẩy");
            notificationTask.setPriority(TaskPriority.LOW);
            notificationTask.setDueDate(LocalDate.now().plusWeeks(5));
            notificationTask.setProjectId(project.getId());
            taskService.create(notificationTask);
        } catch (Exception e) {
            log.warn("Error creating tasks for mobile project", e);
        }
    }
    
    private void createTasksForApiProject(ProjectResponse project, List<UserResponse> users) {
        try {
            CreateTaskRequest archTask = new CreateTaskRequest();
            archTask.setTitle("API Gateway Architecture");
            archTask.setDescription("Thiết kế kiến trúc API Gateway");
            archTask.setPriority(TaskPriority.HIGH);
            archTask.setDueDate(LocalDate.now().plusWeeks(2));
            archTask.setProjectId(project.getId());
            if (!users.isEmpty()) {
                archTask.setAssigneeId(users.get(0).getId());
            }
            taskService.create(archTask);
            
            CreateTaskRequest rateLimitTask = new CreateTaskRequest();
            rateLimitTask.setTitle("Rate Limiting Implementation");
            rateLimitTask.setDescription("Triển khai giới hạn tần suất API");
            rateLimitTask.setPriority(TaskPriority.MEDIUM);
            rateLimitTask.setDueDate(LocalDate.now().plusWeeks(4));
            rateLimitTask.setProjectId(project.getId());
            if (users.size() > 3) {
                rateLimitTask.setAssigneeId(users.get(3).getId());
            }
            taskService.create(rateLimitTask);
            
            CreateTaskRequest docTask = new CreateTaskRequest();
            docTask.setTitle("API Documentation");
            docTask.setDescription("Viết tài liệu API");
            docTask.setPriority(TaskPriority.LOW);
            docTask.setDueDate(LocalDate.now().plusWeeks(6));
            docTask.setProjectId(project.getId());
            if (users.size() > 4) {
                docTask.setAssigneeId(users.get(4).getId());
            }
            taskService.create(docTask);
        } catch (Exception e) {
            log.warn("Error creating tasks for API project", e);
        }
    }
}
