package com.arquiweb.identity.auth.controller;

import com.arquiweb.identity.auth.dto.requests.LoginRequest;
import com.arquiweb.identity.auth.dto.requests.RegisterClientRequest;
import com.arquiweb.identity.auth.dto.responses.AuthResponse;
import com.arquiweb.identity.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/")
@Tag(name = "Auth Manager", description = "Manage functions related to authentication.")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register new client.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Client registered successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Some of the input is invalid for registration."
            )
    })
    @PostMapping("/register")
    public ResponseEntity<Void> registerClient(
            @RequestBody RegisterClientRequest request
    ) {
        UUID clientId = authService.registerClient(request);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/auth/{id}")
                .buildAndExpand(clientId).toUri();

        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

}
