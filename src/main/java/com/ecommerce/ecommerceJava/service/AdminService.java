package com.ecommerce.ecommerceJava.service;

import com.ecommerce.ecommerceJava.dto.admin.DashboardStatsResponse;
import com.ecommerce.ecommerceJava.dto.auth.UserProfileResponse;
import com.ecommerce.ecommerceJava.model.Role;
import com.ecommerce.ecommerceJava.model.User;
import com.ecommerce.ecommerceJava.repository.OrderRepository;
import com.ecommerce.ecommerceJava.repository.ProductRepository;
import com.ecommerce.ecommerceJava.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminService(UserRepository userRepository,
                        ProductRepository productRepository,
                        OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        Double totalRevenue = orderRepository.calculateTotalRevenue();

        long lowStockProducts = productRepository.findAll().stream()
                .filter(p -> p.getStock() <= 5)
                .count();

        return DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue != null ? totalRevenue : 0.0)
                .lowStockProducts(lowStockProducts)
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user ->
                UserProfileResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(user.getRole())
                        .enabled(user.isEnabled())
                        .createdAt(user.getCreatedAt())
                        .build()
        ).toList();
    }

    @Transactional
    public UserProfileResponse updateUserStatus(Long userId, Role role, Boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        if (role != null) {
            user.setRole(role);
        }
        if (enabled != null) {
            user.setEnabled(enabled);
        }

        User updated = userRepository.save(user);
        return UserProfileResponse.builder()
                .id(updated.getId())
                .username(updated.getUsername())
                .email(updated.getEmail())
                .fullName(updated.getFullName())
                .role(updated.getRole())
                .enabled(updated.isEnabled())
                .createdAt(updated.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + userId);
        }
        userRepository.deleteById(userId);
    }
}
