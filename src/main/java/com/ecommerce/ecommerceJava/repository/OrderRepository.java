package com.ecommerce.ecommerceJava.repository;

import com.ecommerce.ecommerceJava.model.Order;
import com.ecommerce.ecommerceJava.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findAllByOrderByOrderDateDesc();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != 'CANCELLED'")
    Double calculateTotalRevenue();

    long count();
}
