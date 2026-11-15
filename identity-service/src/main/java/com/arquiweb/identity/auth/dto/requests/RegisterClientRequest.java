package com.arquiweb.identity.auth.dto.requests;

import com.arquiweb.identity.auth.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public record RegisterClientRequest(
        String email,
        String firstName,
        String lastName,
        String password,
        String passwordRepeat
){
}
