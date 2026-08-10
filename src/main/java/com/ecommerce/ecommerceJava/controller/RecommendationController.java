package com.ecommerce.ecommerceJava.controller;

import com.ecommerce.ecommerceJava.dto.RecommendationRequest;
import com.ecommerce.ecommerceJava.dto.RecommendationResponse;
import com.ecommerce.ecommerceJava.model.Product;
import com.ecommerce.ecommerceJava.service.OpenAIService;
import com.ecommerce.ecommerceJava.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/recommendations")
@Tag(name = "Recommendations", description = "Recomendaciones de productos con Inteligencia Artificial (ChatGPT)")
public class RecommendationController {

    private final OpenAIService openAIService;
    private final ProductService productService;

    public RecommendationController(OpenAIService openAIService, ProductService productService) {
        this.openAIService = openAIService;
        this.productService = productService;
    }

    @Operation(
        summary = "Obtener recomendaciones de productos con IA",
        description = """
                Analiza el catálogo completo de productos registrados en la base de datos
                y utiliza ChatGPT (GPT-4o-mini) para recomendar los más adecuados según
                la consulta del usuario. Puedes filtrar por categoría y presupuesto máximo.
                
                **Ejemplo de consulta:** "Busco algo para el hogar que sea económico"
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recomendaciones generadas correctamente"),
            @ApiResponse(responseCode = "400", description = "La consulta no puede estar vacía"),
            @ApiResponse(responseCode = "500", description = "Error al comunicarse con la API de OpenAI")
    })
    @PostMapping
    public ResponseEntity<?> getRecommendations(@RequestBody RecommendationRequest request) {
        if (request.getQuery() == null || request.getQuery().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("El campo 'query' es obligatorio. Describe qué tipo de producto buscas.");
        }

        // Obtener todos los productos de la base de datos
        List<Product> allProducts = productService.getAllProducts();

        if (allProducts.isEmpty()) {
            return ResponseEntity.ok(new RecommendationResponse(
                    "No hay productos registrados en la base de datos. Por favor, agrega productos primero.",
                    List.of()));
        }

        // Filtrar por categoría si se especificó
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getCategory() != null &&
                            p.getCategory().equalsIgnoreCase(request.getCategory()))
                    .toList();
        }

        // Filtrar por precio máximo si se especificó
        if (request.getMaxPrice() != null) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getPrice() <= request.getMaxPrice())
                    .toList();
        }

        if (allProducts.isEmpty()) {
            return ResponseEntity.ok(new RecommendationResponse(
                    "No se encontraron productos que coincidan con los filtros aplicados " +
                    "(categoría: " + request.getCategory() + ", precio máximo: " + request.getMaxPrice() + ").",
                    List.of()));
        }

        RecommendationResponse response = openAIService.getRecommendations(request, allProducts);
        return ResponseEntity.ok(response);
    }
}
