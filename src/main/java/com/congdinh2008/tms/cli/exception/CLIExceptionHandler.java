package com.congdinh2008.tms.cli.exception;

import com.congdinh2008.tms.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * CLI Exception Handler for user-friendly error messages
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Component
public class CLIExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(CLIExceptionHandler.class);
    
    /**
     * Handle and display user-friendly error messages
     */
    public void handleException(Exception e) {
        log.error("Exception occurred in CLI", e);
        
        if (e instanceof EntityNotFoundException) {
            System.out.println("❌ Không tìm thấy dữ liệu: " + e.getMessage());
        } else if (e instanceof BusinessRuleViolationException) {
            System.out.println("⚠️ Vi phạm quy tắc nghiệp vụ: " + e.getMessage());
        } else if (e instanceof DuplicateEntityException) {
            System.out.println("⚠️ Dữ liệu đã tồn tại: " + e.getMessage());
        } else if (e instanceof CircularReferenceException) {
            System.out.println("⚠️ Phát hiện tham chiếu vòng: " + e.getMessage());
        } else if (e instanceof InvalidAssignmentException) {
            System.out.println("⚠️ Phân công không hợp lệ: " + e.getMessage());
        } else if (e instanceof OptimisticLockingException) {
            System.out.println("⚠️ Dữ liệu đã được cập nhật bởi người khác. Vui lòng thử lại!");
        } else if (e instanceof RepositoryException) {
            System.out.println("❌ Lỗi truy xuất dữ liệu: " + e.getMessage());
        } else if (e.getMessage() != null && e.getMessage().contains("validation")) {
            System.out.println("❌ Dữ liệu không hợp lệ: " + e.getMessage());
        } else {
            System.out.println("💥 Lỗi hệ thống: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            log.error("Unexpected error in CLI", e);
        }
    }
    
    /**
     * Handle exception with additional context
     */
    public void handleException(Exception e, String context) {
        log.error("Exception occurred in CLI context: {}", context, e);
        System.out.println("❌ Lỗi trong " + context + ": " + e.getMessage());
    }
}
