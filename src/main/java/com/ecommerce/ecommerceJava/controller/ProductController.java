package com.ecommerce.ecommerceJava.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerceJava.model.Product;
import com.ecommerce.ecommerceJava.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Productos", description = "Endpoints para la gestión y consulta del catálogo de productos")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Obtener todos los productos", description = "Retorna el catálogo completo de productos. Permite filtrar opcionalmente por categoryId. Acceso público.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(productService.getAllProducts(categoryId));
    }

    @Operation(summary = "Obtener un producto por ID", description = "Retorna el detalle de un producto específico. Acceso público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @Operation(
            summary = "Crear un nuevo producto [Admin]",
            description = "Crea un producto en el catálogo. Requiere rol ROLE_ADMIN.",
            security = @SecurityRequirement(name = "BearerAuthentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Permiso denegado (Requiere ADMIN)")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Actualizar un producto [Admin]",
            description = "Actualiza los datos de un producto existente. Requiere rol ROLE_ADMIN.",
            security = @SecurityRequirement(name = "BearerAuthentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "403", description = "Permiso denegado (Requiere ADMIN)")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updated = productService.updateProduct(id, product);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Eliminar un producto [Admin]",
            description = "Elimina un producto del catálogo por su ID. Requiere rol ROLE_ADMIN.",
            security = @SecurityRequirement(name = "BearerAuthentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "403", description = "Permiso denegado (Requiere ADMIN)")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
