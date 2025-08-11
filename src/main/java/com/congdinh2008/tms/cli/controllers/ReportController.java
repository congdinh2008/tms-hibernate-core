package com.congdinh2008.tms.cli.controllers;

import com.congdinh2008.tms.cli.helpers.InputHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * CLI Controller for Report operations
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Component
public class ReportController {
    
    private final InputHelper inputHelper;
    
    @Autowired
    public ReportController(InputHelper inputHelper) {
        this.inputHelper = inputHelper;
    }
    
    public void handleReportMenu() {
        while (true) {
            displayReportMenu();
            int choice = inputHelper.getIntInput("Chọn báo cáo: ");
            
            switch (choice) {
                case 1 -> showProjectStatistics();
                case 2 -> showUserProductivity();
                case 0 -> { return; }
                default -> System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
    
    private void displayReportMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("📊 BÁO CÁO VÀ THỐNG KÊ");
        System.out.println("=".repeat(40));
        System.out.println("1. 📈 Thống kê dự án");
        System.out.println("2. 👤 Năng suất người dùng");
        System.out.println("0. ⬅️ Quay lại");
        System.out.println("=".repeat(40));
    }
    
    private void showProjectStatistics() {
        System.out.println("🔄 Tính năng thống kê dự án đang được phát triển...");
    }
    
    private void showUserProductivity() {
        System.out.println("🔄 Tính năng thống kê năng suất đang được phát triển...");
    }
}
