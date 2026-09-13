package com.ecommerce.ecommerceJava.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productCategory;
    private String productImageUrl;
    private int quantity;
    private double price;
    private double subtotal;
}
