package com.ecommerce.ecommerceJava.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponse {
    private long totalUsers;
    private long totalProducts;
    private long totalOrders;
    private double totalRevenue;
    private long lowStockProducts;
}
