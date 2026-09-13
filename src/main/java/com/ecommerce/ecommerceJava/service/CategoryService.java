package com.ecommerce.ecommerceJava.service;

import com.ecommerce.ecommerceJava.model.Category;
import com.ecommerce.ecommerceJava.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories(boolean onlyActive) {
        if (onlyActive) {
            return categoryRepository.findByActivaTrueOrderByOrdenAsc();
        }
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con slug: " + slug));
    }

    @Transactional
    public Category createCategory(Category category) {
        if (category.getSlug() == null || category.getSlug().isBlank()) {
            category.setSlug(generateSlug(category.getNombre()));
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category updated) {
        Category existing = getCategoryById(id);
        if (updated.getNombre() != null) existing.setNombre(updated.getNombre());
        if (updated.getDescripcion() != null) existing.setDescripcion(updated.getDescripcion());
        if (updated.getSlug() != null) existing.setSlug(updated.getSlug());
        if (updated.getOrden() != null) existing.setOrden(updated.getOrden());
        existing.setActiva(updated.isActiva());
        return categoryRepository.save(existing);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Categoría no encontrada con ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private String generateSlug(String text) {
        if (text == null) return "";
        return text.trim().toLowerCase()
                .replaceAll("[áàäâ]", "a")
                .replaceAll("[éèëê]", "e")
                .replaceAll("[íìïî]", "i")
                .replaceAll("[óòöô]", "o")
                .replaceAll("[úùüû]", "u")
                .replaceAll("[ñ]", "n")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
