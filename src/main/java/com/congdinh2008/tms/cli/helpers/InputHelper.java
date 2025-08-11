package com.congdinh2008.tms.cli.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Helper class for handling user input in CLI
 * 
 * @author congdinh2008
 * @since 1.0.0
 */
@Component
public class InputHelper {
    
    private static final String EMPTY_INPUT_MESSAGE = "❌ Không được để trống!";
    private static final String INVALID_NUMBER_MESSAGE = "❌ Vui lòng nhập số nguyên hợp lệ!";
    private static final String INVALID_LONG_MESSAGE = "❌ Vui lòng nhập số hợp lệ!";
    private static final String INVALID_CHOICE_MESSAGE = "❌ Lựa chọn không hợp lệ!";
    private static final String INVALID_DATE_MESSAGE = "❌ Định dạng ngày không hợp lệ! Vui lòng nhập theo định dạng yyyy-MM-dd";
    private static final String INVALID_DATETIME_MESSAGE = "❌ Định dạng ngày giờ không hợp lệ! Vui lòng nhập theo định dạng yyyy-MM-dd HH:mm";
    
    private final Scanner scanner;
    
    @Autowired
    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }
    
    /**
     * Get string input from user with validation
     */
    public String getStringInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println(EMPTY_INPUT_MESSAGE);
            return getStringInput(prompt);
        }
        return input;
    }
    
    /**
     * Get optional string input (can be empty)
     */
    public String getOptionalStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    /**
     * Get integer input with validation
     */
    public Integer getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(EMPTY_INPUT_MESSAGE);
                return getIntInput(prompt);
            }
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(INVALID_NUMBER_MESSAGE);
            return getIntInput(prompt);
        }
    }
    
    /**
     * Get optional integer input
     */
    public Integer getOptionalIntInput(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(INVALID_NUMBER_MESSAGE);
            return getOptionalIntInput(prompt);
        }
    }
    
    /**
     * Get long input with validation
     */
    public Long getLongInput(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(EMPTY_INPUT_MESSAGE);
                return getLongInput(prompt);
            }
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println(INVALID_LONG_MESSAGE);
            return getLongInput(prompt);
        }
    }
    
    /**
     * Get optional long input
     */
    public Long getOptionalLongInput(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println(INVALID_LONG_MESSAGE);
            return getOptionalLongInput(prompt);
        }
    }
    
    /**
     * Get enum input with user-friendly selection
     */
    public <T extends Enum<T>> T getEnumInput(String prompt, Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        System.out.println("\n" + prompt + ":");
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + values[i]);
        }
        
        int choice = getIntInput("Chọn (1-" + values.length + "): ");
        if (choice < 1 || choice > values.length) {
            System.out.println(INVALID_CHOICE_MESSAGE);
            return getEnumInput(prompt, enumClass);
        }
        return values[choice - 1];
    }
    
    /**
     * Get optional enum input
     */
    public <T extends Enum<T>> T getOptionalEnumInput(String prompt, Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        System.out.println("\n" + prompt + ":");
        System.out.println("0. Bỏ qua");
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + values[i]);
        }
        
        int choice = getIntInput("Chọn (0-" + values.length + "): ");
        if (choice == 0) {
            return null;
        }
        if (choice < 1 || choice > values.length) {
            System.out.println(INVALID_CHOICE_MESSAGE);
            return getOptionalEnumInput(prompt, enumClass);
        }
        return values[choice - 1];
    }
    
    /**
     * Get yes/no input
     */
    public boolean getYesNoInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes") || input.equals("có") || input.equals("c");
    }
    
    /**
     * Get date input (yyyy-MM-dd format)
     */
    public LocalDate getDateInput(String prompt) {
        System.out.print(prompt + " (định dạng: yyyy-MM-dd): ");
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(EMPTY_INPUT_MESSAGE);
                return getDateInput(prompt);
            }
            return LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            System.out.println(INVALID_DATE_MESSAGE);
            return getDateInput(prompt);
        }
    }
    
    /**
     * Get optional date input
     */
    public LocalDate getOptionalDateInput(String prompt) {
        System.out.print(prompt + " (định dạng: yyyy-MM-dd, để trống để bỏ qua): ");
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            return LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            System.out.println(INVALID_DATE_MESSAGE);
            return getOptionalDateInput(prompt);
        }
    }
    
    /**
     * Get datetime input (yyyy-MM-dd HH:mm format)
     */
    public LocalDateTime getDateTimeInput(String prompt) {
        System.out.print(prompt + " (định dạng: yyyy-MM-dd HH:mm): ");
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(EMPTY_INPUT_MESSAGE);
                return getDateTimeInput(prompt);
            }
            return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println(INVALID_DATETIME_MESSAGE);
            return getDateTimeInput(prompt);
        }
    }
    
    /**
     * Get optional datetime input
     */
    public LocalDateTime getOptionalDateTimeInput(String prompt) {
        System.out.print(prompt + " (định dạng: yyyy-MM-dd HH:mm, để trống để bỏ qua): ");
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println(INVALID_DATETIME_MESSAGE);
            return getOptionalDateTimeInput(prompt);
        }
    }
    
    /**
     * Wait for user to press Enter
     */
    public void waitForEnter() {
        System.out.print("\nNhấn Enter để tiếp tục...");
        scanner.nextLine();
    }
    
    /**
     * Wait for user to press Enter with custom message
     */
    public void waitForEnter(String message) {
        System.out.print("\n" + message);
        scanner.nextLine();
    }
}
