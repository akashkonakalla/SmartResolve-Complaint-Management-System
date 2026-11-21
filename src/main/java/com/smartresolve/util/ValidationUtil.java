package com.smartresolve.util;

import java.util.regex.Pattern;

/**
 * ValidationUtil centralizes input validation and string sanitization logic.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[0-9]{7,15}$"
    );

    private ValidationUtil() {
        // Prevent instantiation
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) {
            return true; // Phone is optional in some contexts
        }
        return PHONE_PATTERN.matcher(phone.trim().replaceAll("[\\s-]", "")).matches();
    }

    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 6;
    }

    public static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }
}
