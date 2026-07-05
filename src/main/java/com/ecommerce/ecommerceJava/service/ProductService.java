package com.ecommerce.ecommerceJava.service;

import java.util.List;

import com.ecommerce.ecommerceJava.model.Product;
import com.ecommerce.ecommerceJava.repository.ProductRepository;

public class ProductService {
    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public Product getProductById(Long id){
        return productRepository.findById(id).orElse(null);
    }
    public Product createProduct(Product product){
        return productRepository.save(product);
    }
    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }
}
