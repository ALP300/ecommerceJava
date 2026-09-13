package com.ecommerce.ecommerceJava.dto.order;

import com.ecommerce.ecommerceJava.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String username;
    private String userEmail;
    private LocalDateTime orderDate;
    private double totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private List<OrderItemResponse> items;
}
