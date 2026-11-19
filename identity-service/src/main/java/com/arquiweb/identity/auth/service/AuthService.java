package com.arquiweb.identity.auth.service;

import com.arquiweb.identity.auth.domain.User;
import com.arquiweb.identity.auth.domain.exceptions.InvalidCredentialsException;
import com.arquiweb.identity.auth.dto.requests.LoginRequest;
import com.arquiweb.identity.auth.dto.requests.RegisterClientRequest;
import com.arquiweb.identity.auth.dto.responses.AuthResponse;
import com.arquiweb.identity.auth.repository.UserRepository;
import com.arquiweb.identity.infrastructure.exception.InvalidInputException;
import com.arquiweb.identity.infrastructure.exception.ResourceNotFoundException;
import com.arquiweb.identity.infrastructure.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public UUID registerClient(RegisterClientRequest request) {
        User client = User.registerClient(
                request.email(),
                request.firstName(),
                request.lastName(),
                request.password(),
                request.passwordRepeat(),
                passwordEncoder
        );

        client = userRepository.save(client);

        return client.getId();
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().toLowerCase().trim();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given email."));

        if (!user.verifyPassword(request.password(), passwordEncoder)) throw new InvalidCredentialsException("Given password is not correct.");

        String token = jwtProvider.generateToken(user);

        return AuthResponse.bearer(
                token,
                jwtProvider.getExpirationMs(),
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No user with this ID."));
    }

    public List<User> getAllById(List<UUID> userIds) {
        return userRepository.findAllById(userIds);
    }

}
