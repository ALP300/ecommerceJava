package com.ecommerce.ecommerceJava.service;

import com.ecommerce.ecommerceJava.dto.order.CreateOrderRequest;
import com.ecommerce.ecommerceJava.dto.order.OrderItemRequest;
import com.ecommerce.ecommerceJava.dto.order.OrderItemResponse;
import com.ecommerce.ecommerceJava.dto.order.OrderResponse;
import com.ecommerce.ecommerceJava.model.Order;
import com.ecommerce.ecommerceJava.model.OrderItem;
import com.ecommerce.ecommerceJava.model.OrderStatus;
import com.ecommerce.ecommerceJava.model.Product;
import com.ecommerce.ecommerceJava.model.User;
import com.ecommerce.ecommerceJava.repository.OrderRepository;
import com.ecommerce.ecommerceJava.repository.ProductRepository;
import com.ecommerce.ecommerceJava.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .items(new ArrayList<>())
                .build();

        double totalAmount = 0.0;

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + itemReq.getProductId()));

            if (product.getStock() < itemReq.getQuantity()) {
                throw new IllegalStateException("Stock insuficiente para el producto '" + product.getName()
                        + "'. Stock disponible: " + product.getStock() + ", solicitado: " + itemReq.getQuantity());
            }

            // Descontar inventario
            product.setStock(product.getStock() - itemReq.getQuantity());
            productRepository.save(product);

            double itemTotal = product.getPrice() * itemReq.getQuantity();
            totalAmount += itemTotal;

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return orderRepository.findByUserOrderByOrderDateDesc(user).stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc().stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long requestingUserId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con ID: " + orderId));

        if (!isAdmin && !order.getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("No tienes permiso para ver esta orden");
        }

        return mapToOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con ID: " + orderId));

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream().map(item ->
                OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productCategory(item.getProduct().getCategory() != null ? item.getProduct().getCategory().getNombre() : null)
                        .productImageUrl(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getPrice() * item.getQuantity())
                        .build()
        ).toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .username(order.getUser().getUsername())
                .userEmail(order.getUser().getEmail())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .items(itemResponses)
                .build();
    }
}
