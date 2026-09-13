package com.ecommerce.ecommerceJava.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {

    @NotEmpty(message = "La orden debe contener al menos un producto")
    @Valid
    private List<OrderItemRequest> items;

    private String shippingAddress;
}
