package com.ecommerce.ecommerceJava.controller;

import com.ecommerce.ecommerceJava.dto.order.CreateOrderRequest;
import com.ecommerce.ecommerceJava.dto.order.OrderResponse;
import com.ecommerce.ecommerceJava.model.Role;
import com.ecommerce.ecommerceJava.security.services.UserDetailsImpl;
import com.ecommerce.ecommerceJava.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Órdenes", description = "Endpoints para la gestión y creación de pedidos de compra")
@SecurityRequirement(name = "BearerAuthentication")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Crear un nuevo pedido [Usuario / Admin]",
            description = "Registra un pedido para el usuario autenticado, valida y descuenta el stock de los productos."
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OrderResponse response = orderService.createOrder(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Obtener mis pedidos [Usuario / Admin]",
            description = "Retorna la lista de pedidos realizados por el usuario actualmente autenticado."
    )
    @GetMapping("/my-orders")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<OrderResponse> orders = orderService.getUserOrders(userDetails.getId());
        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Obtener detalle de un pedido por ID",
            description = "Retorna el detalle completo de un pedido. El usuario solo puede consultar sus propios pedidos a menos que sea ADMIN."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean isAdmin = Role.ROLE_ADMIN.name().equals(userDetails.getRole());
        OrderResponse order = orderService.getOrderById(id, userDetails.getId(), isAdmin);
        return ResponseEntity.ok(order);
    }
}
