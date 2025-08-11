package com.congdinh2008.tms.cli.controllers;

import com.congdinh2008.tms.cli.helpers.InputHelper;
import com.congdinh2008.tms.cli.helpers.DisplayHelper;
import com.congdinh2008.tms.cli.exception.CLIExceptionHandler;
import com.congdinh2008.tms.services.UserService;
import com.congdinh2008.tms.dto.request.CreateUserRequest;
import com.congdinh2008.tms.dto.request.UpdateUserRequest;
import com.congdinh2008.tms.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CLI Controller for User Management
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Component
public class UserController {
    
    private final UserService userService;
    private final InputHelper inputHelper;
    private final DisplayHelper displayHelper;
    private final CLIExceptionHandler exceptionHandler;
    
    @Autowired
    public UserController(UserService userService,
                         InputHelper inputHelper,
                         DisplayHelper displayHelper,
                         CLIExceptionHandler exceptionHandler) {
        this.userService = userService;
        this.inputHelper = inputHelper;
        this.displayHelper = displayHelper;
        this.exceptionHandler = exceptionHandler;
    }
    
    public void handleUserMenu() {
        while (true) {
            displayUserMenu();
            int choice = inputHelper.getIntInput("Chọn chức năng: ");
            
            try {
                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> viewAllUsers();
                    case 3 -> viewUserById();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> { return; }
                    default -> System.out.println("❌ Lựa chọn không hợp lệ!");
                }
            } catch (Exception e) {
                exceptionHandler.handleException(e);
            }
        }
    }
    
    private void displayUserMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("👥 QUẢN LÝ NGƯỜI DÙNG");
        System.out.println("=".repeat(50));
        System.out.println("1. ➕ Tạo người dùng mới");
        System.out.println("2. 👥 Xem tất cả người dùng");
        System.out.println("3. 🔍 Xem chi tiết người dùng");
        System.out.println("4. ✏️ Cập nhật người dùng");
        System.out.println("5. 🗑️ Xóa người dùng");
        System.out.println("0. ⬅️ Quay lại");
        System.out.println("=".repeat(50));
    }
    
    private void createUser() {
        System.out.println("\n➕ TẠO NGƯỜI DÙNG MỚI");
        System.out.println("=".repeat(30));
        
        String username = inputHelper.getStringInput("Tên đăng nhập: ");
        String email = inputHelper.getStringInput("Email: ");
        String password = inputHelper.getStringInput("Mật khẩu: ");
        String fullName = inputHelper.getStringInput("Họ và tên: ");
        
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setFullName(fullName);
        
        UserResponse user = userService.create(request);
        System.out.println("✅ Tạo người dùng thành công!");
        displayHelper.displayUserResponse(user);
    }
    
    private void viewAllUsers() {
        System.out.println("\n👥 DANH SÁCH TẤT CẢ NGƯỜI DÙNG");
        System.out.println("=".repeat(40));
        
        List<UserResponse> users = userService.getAll();
        if (users.isEmpty()) {
            System.out.println("📭 Không có người dùng nào.");
            return;
        }
        
        users.forEach(displayHelper::displayUserResponse);
    }
    
    private void viewUserById() {
        System.out.println("\n🔍 XEM CHI TIẾT NGƯỜI DÙNG");
        System.out.println("=".repeat(30));
        
        Long userId = inputHelper.getLongInput("Nhập ID người dùng: ");
        try {
            UserResponse user = userService.getById(userId);
            displayHelper.displayUserResponse(user);
        } catch (Exception e) {
            System.out.println("❌ Không tìm thấy người dùng với ID: " + userId);
        }
    }
    
    private void updateUser() {
        System.out.println("\n✏️ CẬP NHẬT NGƯỜI DÙNG");
        System.out.println("=".repeat(30));
        
        Long userId = inputHelper.getLongInput("Nhập ID người dùng cần cập nhật: ");
        try {
            UserResponse existingUser = userService.getById(userId);
            
            System.out.println("👤 Thông tin hiện tại:");
            displayHelper.displayUserResponse(existingUser);
            
            System.out.println("\n📝 Nhập thông tin mới (Enter để giữ nguyên):");
            
            String username = inputHelper.getStringInput("Tên đăng nhập [" + existingUser.getUsername() + "]: ");
            if (username.trim().isEmpty()) username = existingUser.getUsername();
            
            String email = inputHelper.getStringInput("Email [" + existingUser.getEmail() + "]: ");
            if (email.trim().isEmpty()) email = existingUser.getEmail();
            
            String fullName = inputHelper.getStringInput("Họ và tên [" + existingUser.getFullName() + "]: ");
            if (fullName.trim().isEmpty()) fullName = existingUser.getFullName();
            
            UpdateUserRequest request = new UpdateUserRequest();
            request.setUsername(username);
            request.setEmail(email);
            request.setFullName(fullName);
            
            UserResponse updatedUser = userService.update(userId, request);
            System.out.println("✅ Cập nhật người dùng thành công!");
            displayHelper.displayUserResponse(updatedUser);
        } catch (Exception e) {
            System.out.println("❌ Không tìm thấy người dùng với ID: " + userId);
        }
    }
    
    private void deleteUser() {
        System.out.println("\n🗑️ XÓA NGƯỜI DÙNG");
        System.out.println("=".repeat(20));
        
        Long userId = inputHelper.getLongInput("Nhập ID người dùng cần xóa: ");
        try {
            UserResponse user = userService.getById(userId);
            
            System.out.println("👤 Thông tin người dùng sẽ bị xóa:");
            displayHelper.displayUserResponse(user);
            
            boolean confirm = inputHelper.getYesNoInput("Bạn có chắc chắn muốn xóa? (y/n): ");
            if (confirm) {
                userService.delete(userId);
                System.out.println("✅ Đã xóa người dùng thành công!");
            } else {
                System.out.println("❌ Đã hủy thao tác xóa.");
            }
        } catch (Exception e) {
            System.out.println("❌ Không tìm thấy người dùng với ID: " + userId);
        }
    }
}
