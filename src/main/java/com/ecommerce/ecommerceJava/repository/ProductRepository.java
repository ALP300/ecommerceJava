package com.ecommerce.ecommerceJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerceJava.model.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {
    
}
