package com.ecommerce.ecommerceJava.service;

import java.util.List;

import com.ecommerce.ecommerceJava.model.Category;
import com.ecommerce.ecommerceJava.model.Product;
import com.ecommerce.ecommerceJava.repository.CategoryRepository;
import com.ecommerce.ecommerceJava.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return getAllProducts(null);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts(Long categoryId) {
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public Product createProduct(Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + product.getCategory().getId()));
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id).map(existing -> {
            if (updatedProduct.getName() != null) existing.setName(updatedProduct.getName());
            if (updatedProduct.getDescription() != null) existing.setDescription(updatedProduct.getDescription());
            if (updatedProduct.getPrice() > 0) existing.setPrice(updatedProduct.getPrice());
            if (updatedProduct.getStock() >= 0) existing.setStock(updatedProduct.getStock());
            if (updatedProduct.getImageUrl() != null) existing.setImageUrl(updatedProduct.getImageUrl());

            if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getId() != null) {
                Category category = categoryRepository.findById(updatedProduct.getCategory().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + updatedProduct.getCategory().getId()));
                existing.setCategory(category);
            }
            return productRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado con el id: " + id));
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
