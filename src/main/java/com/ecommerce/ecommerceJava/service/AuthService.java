package com.ecommerce.ecommerceJava.service;

import com.ecommerce.ecommerceJava.dto.auth.AuthResponse;
import com.ecommerce.ecommerceJava.dto.auth.LoginRequest;
import com.ecommerce.ecommerceJava.dto.auth.RegisterRequest;
import com.ecommerce.ecommerceJava.dto.auth.UserProfileResponse;
import com.ecommerce.ecommerceJava.model.Role;
import com.ecommerce.ecommerceJava.model.User;
import com.ecommerce.ecommerceJava.repository.UserRepository;
import com.ecommerce.ecommerceJava.security.jwt.JwtUtils;
import com.ecommerce.ecommerceJava.security.services.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Error: El nombre de usuario ya está en uso.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Error: El correo electrónico ya está registrado.");
        }

        Role assignedRole = request.getRole() != null ? request.getRole() : Role.ROLE_USER;

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(assignedRole)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtUtils.generateTokenFromUsername(
                savedUser.getUsername(),
                savedUser.getId(),
                savedUser.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .message("Usuario registrado exitosamente.")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtils.generateJwtToken(authentication);

        Role role = Role.valueOf(userDetails.getRole());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .fullName(userDetails.getFullName())
                .role(role)
                .message("Inicio de sesión exitoso.")
                .build();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new org.springframework.security.access.AccessDeniedException("No hay un usuario autenticado.");
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
