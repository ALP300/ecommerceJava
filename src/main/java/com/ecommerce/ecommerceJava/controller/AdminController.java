package com.ecommerce.ecommerceJava.controller;

import com.ecommerce.ecommerceJava.dto.admin.DashboardStatsResponse;
import com.ecommerce.ecommerceJava.dto.admin.UserStatusUpdateRequest;
import com.ecommerce.ecommerceJava.dto.auth.UserProfileResponse;
import com.ecommerce.ecommerceJava.dto.order.OrderResponse;
import com.ecommerce.ecommerceJava.model.OrderStatus;
import com.ecommerce.ecommerceJava.service.AdminService;
import com.ecommerce.ecommerceJava.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Panel de Administración", description = "Endpoints exclusivos para la administración del sistema y estadísticas [ROLE_ADMIN]")
@SecurityRequirement(name = "BearerAuthentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final OrderService orderService;

    public AdminController(AdminService adminService, OrderService orderService) {
        this.adminService = adminService;
        this.orderService = orderService;
    }

    @Operation(summary = "Métricas del Dashboard [Admin]", description = "Obtiene estadísticas generales: total de usuarios, productos, órdenes, ingresos y productos con bajo stock.")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Listar todos los usuarios [Admin]", description = "Retorna la lista completa de usuarios registrados.")
    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Actualizar rol o estado de un usuario [Admin]", description = "Permite cambiar el rol (ROLE_USER, ROLE_ADMIN) o habilitar/deshabilitar una cuenta.")
    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserProfileResponse> updateUserStatus(@PathVariable Long id,
                                                                @RequestBody UserStatusUpdateRequest request) {
        UserProfileResponse updated = adminService.updateUserStatus(id, request.getRole(), request.getEnabled());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar un usuario [Admin]", description = "Elimina permanentemente una cuenta de usuario.")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todos los pedidos [Admin]", description = "Retorna el historial completo de todos los pedidos de la tienda.")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Actualizar estado de un pedido [Admin]", description = "Cambia el estado de un pedido (PENDING, PAID, SHIPPED, DELIVERED, CANCELLED).")
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id,
                                                           @RequestParam OrderStatus status) {
        OrderResponse updated = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
