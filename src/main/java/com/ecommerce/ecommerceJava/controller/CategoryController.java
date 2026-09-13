package com.ecommerce.ecommerceJava.controller;

import com.ecommerce.ecommerceJava.model.Category;
import com.ecommerce.ecommerceJava.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categorías", description = "Endpoints para la gestión y consulta de categorías de productos")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Obtener todas las categorías", description = "Retorna las categorías disponibles. Acceso público.")
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam(required = false, defaultValue = "false") boolean all) {
        return ResponseEntity.ok(categoryService.getAllCategories(!all));
    }

    @Operation(summary = "Obtener categoría por ID", description = "Retorna el detalle de una categoría específica. Acceso público.")
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(
            summary = "Crear nueva categoría [Admin]",
            description = "Crea una categoría en la tienda. Requiere rol ROLE_ADMIN.",
            security = @SecurityRequirement(name = "BearerAuthentication")
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Actualizar categoría [Admin]",
            description = "Actualiza los datos de una categoría existente. Requiere rol ROLE_ADMIN.",
            security = @SecurityRequirement(name = "BearerAuthentication")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Eliminar categoría [Admin]",
            description = "Elimina una categoría del sistema. Requiere rol ROLE_ADMIN.",
            security = @SecurityRequirement(name = "BearerAuthentication")
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
