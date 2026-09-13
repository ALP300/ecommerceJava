package com.ecommerce.ecommerceJava.controller;

import com.ecommerce.ecommerceJava.dto.auth.AuthResponse;
import com.ecommerce.ecommerceJava.dto.auth.LoginRequest;
import com.ecommerce.ecommerceJava.dto.auth.RegisterRequest;
import com.ecommerce.ecommerceJava.dto.auth.UserProfileResponse;
import com.ecommerce.ecommerceJava.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para registro, inicio de sesión y perfil de usuario con JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta de usuario en el sistema. Por defecto asigna ROLE_USER.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y retorna un token JWT válido junto con los datos del usuario.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtener perfil del usuario actual",
            description = "Retorna los datos del usuario autenticado mediante el token JWT en la cabecera Authorization.",
            security = @SecurityRequirement(name = "BearerAuthentication")
    )
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        UserProfileResponse profile = authService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }
}
