package com.arquiweb.identity.auth.domain;

import com.arquiweb.identity.auth.domain.enums.UserRole;
import com.arquiweb.identity.infrastructure.exception.InvalidInputException;
import com.arquiweb.identity.infrastructure.util.CredentialValidator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private User(
            String email,
            String firstName,
            String lastName,
            String passwordHash,
            UserRole role
    ) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = passwordHash;
        this.role = role;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public static User registerClient(
            String email,
            String firstName,
            String lastName,
            String password,
            String passwordRepeat,
            PasswordEncoder encoder
    ) {
        Map<String, String> errors = new HashMap<>();

        if (email == null || email.isBlank()) errors.put("email", "Email cannot be null or blank.");
        if (!CredentialValidator.isValidEmail(email)) errors.put("email", "Email is not valid.");

        if (firstName == null || firstName.isBlank()) errors.put("firstName", "First name cannot be null or blank.");
        if (lastName == null || lastName.isBlank()) errors.put("lastName", "Last name cannot be null or blank.");

        if (password == null || password.isBlank()) errors.put("password", "Password cannot be null or blank.");
        if (passwordRepeat == null || passwordRepeat.isBlank()) errors.put("password", "Password repeat cannot be null or blank.");

        if (password != null && passwordRepeat != null && !password.equals(passwordRepeat)) errors.put("password", "Passwords must match.");

        if (!CredentialValidator.isValidPassword(password)) errors.put("password", "Password is not valid. Must have at least 8 characters, 1 upper-case, 1 lower-case, 1 digit and 1 special character.");

        if (!errors.isEmpty()) throw new InvalidInputException("Given user has invalid field/s.", errors);

        String passwordHash = encoder.encode(password);

        return new User(
                email.toLowerCase().trim(),
                firstName,
                lastName,
                passwordHash,
                UserRole.CLIENT
        );
    }

    public boolean verifyPassword(String rawPassword, PasswordEncoder encoder) {
        if (rawPassword == null || rawPassword.isBlank()) return false;
        return encoder.matches(rawPassword, this.password);
    }
}