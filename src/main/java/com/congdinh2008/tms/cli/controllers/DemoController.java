package com.congdinh2008.tms.cli.controllers;

import com.congdinh2008.tms.cli.helpers.InputHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * CLI Controller for Advanced Feature Demos
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Component
public class DemoController {
    
    private final InputHelper inputHelper;
    
    @Autowired
    public DemoController(InputHelper inputHelper) {
        this.inputHelper = inputHelper;
    }
    
    public void handleAdvancedDemo() {
        while (true) {
            displayDemoMenu();
            int choice = inputHelper.getIntInput("Chọn demo: ");
            
            switch (choice) {
                case 1 -> demoOptimisticLocking();
                case 2 -> demoSecondLevelCache();
                case 3 -> demoBusinessRules();
                case 0 -> { return; }
                default -> System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
    
    private void displayDemoMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🚀 DEMO CÁC TÍNH NĂNG NÂNG CAO");
        System.out.println("=".repeat(50));
        System.out.println("1. 🔒 Optimistic Locking Demo");
        System.out.println("2. 💾 Second Level Cache Demo");
        System.out.println("3. 📋 Business Rules Demo");
        System.out.println("0. ⬅️ Quay lại");
        System.out.println("=".repeat(50));
    }
    
    private void demoOptimisticLocking() {
        System.out.println("\n📌 Demo Optimistic Locking:");
        System.out.println("🔄 Tính năng demo Optimistic Locking đang được phát triển...");
        System.out.println("Optimistic Locking giúp đảm bảo tính toàn vẹn dữ liệu khi có nhiều người cùng chỉnh sửa.");
    }
    
    private void demoSecondLevelCache() {
        System.out.println("\n📌 Demo Second Level Cache:");
        System.out.println("🔄 Tính năng demo Second Level Cache đang được phát triển...");
        System.out.println("Second Level Cache giúp cải thiện hiệu suất truy xuất dữ liệu.");
    }
    
    private void demoBusinessRules() {
        System.out.println("\n📌 Demo Business Rules:");
        System.out.println("🔄 Tính năng demo Business Rules đang được phát triển...");
        System.out.println("Business Rules đảm bảo logic nghiệp vụ được thực thi đúng.");
    }
}
