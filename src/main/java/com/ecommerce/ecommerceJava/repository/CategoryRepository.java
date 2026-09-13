package com.ecommerce.ecommerceJava.repository;

import com.ecommerce.ecommerceJava.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByActivaTrueOrderByOrdenAsc();
    Optional<Category> findBySlug(String slug);
    boolean existsByNombre(String nombre);
    boolean existsBySlug(String slug);
}
