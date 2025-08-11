package com.congdinh2008.tms.cli.controllers;

import com.congdinh2008.tms.cli.helpers.InputHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * CLI Controller for Tag operations
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Component
public class TagController {
    
    private final InputHelper inputHelper;
    
    @Autowired
    public TagController(InputHelper inputHelper) {
        this.inputHelper = inputHelper;
    }
    
    public void handleTagMenu() {
        while (true) {
            displayTagMenu();
            int choice = inputHelper.getIntInput("Chọn chức năng: ");
            
            switch (choice) {
                case 1 -> createTag();
                case 2 -> viewAllTags();
                case 0 -> { return; }
                default -> System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
    
    private void displayTagMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("🏷️ QUẢN LÝ TAGS");
        System.out.println("=".repeat(40));
        System.out.println("1. ➕ Tạo tag mới");
        System.out.println("2. 📄 Xem tất cả tags");
        System.out.println("0. ⬅️ Quay lại");
        System.out.println("=".repeat(40));
    }
    
    private void createTag() {
        System.out.println("🔄 Tính năng tạo tag đang được phát triển...");
    }
    
    private void viewAllTags() {
        System.out.println("🔄 Tính năng xem tags đang được phát triển...");
    }
}
