package com.ecommerce.ecommerceJava.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con los productos recomendados por la IA")
public class RecommendationResponse {

    @Schema(description = "Explicación de la IA sobre las recomendaciones")
    private String aiExplanation;

    @Schema(description = "Lista de productos recomendados de la base de datos")
    private List<RecommendedProduct> recommendations;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "Detalle de un producto recomendado")
    public static class RecommendedProduct {
        private Long id;
        private String name;
        private String description;
        private double price;
        private int stock;
        private String category;
        private String reason; // Razón de la recomendación dada por la IA
    }
}
