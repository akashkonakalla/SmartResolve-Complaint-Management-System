package com.smartresolve.service;

import com.smartresolve.dao.UserDAO;
import com.smartresolve.model.User;
import com.smartresolve.util.PasswordUtil;
import com.smartresolve.util.ValidationUtil;

import java.sql.SQLException;

/**
 * AuthService coordinates authentication, registration, and user governance business rules.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new regular user account.
     * Enforces public role protection: Public registrations are ALWAYS assigned the USER role.
     * 
     * @param fullName User's full name
     * @param email User's email
     * @param phone User's contact number
     * @param password Desired plaintext password
     * @param confirmPassword Password confirmation
     * @return created User entity
     * @throws IllegalArgumentException on validation failures
     * @throws SQLException on database errors
     */
    public User registerUser(String fullName, String email, String phone, String password, String confirmPassword) 
            throws IllegalArgumentException, SQLException {

        String sanitizedName = ValidationUtil.sanitize(fullName);
        String sanitizedEmail = ValidationUtil.sanitize(email).toLowerCase();
        String sanitizedPhone = ValidationUtil.sanitize(phone);

        // Validation
        if (!ValidationUtil.isNotEmpty(sanitizedName)) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (!ValidationUtil.isValidEmail(sanitizedEmail)) {
            throw new IllegalArgumentException("Please provide a valid email address.");
        }
        if (sanitizedPhone != null && !sanitizedPhone.isEmpty() && !ValidationUtil.isValidPhone(sanitizedPhone)) {
            throw new IllegalArgumentException("Please provide a valid phone number (7-15 digits).");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // Duplicate email check
        if (userDAO.emailExists(sanitizedEmail)) {
            throw new IllegalArgumentException("An account with this email address already exists.");
        }

        // Hash password securely with BCrypt
        String passwordHash = PasswordUtil.hashPassword(password);

        // Role is strictly locked to USER for public registration
        User newUser = new User(sanitizedName, sanitizedEmail, passwordHash, sanitizedPhone, User.ROLE_USER);
        int generatedId = userDAO.create(newUser);
        newUser.setId(generatedId);

        return newUser;
    }
}
