package com.ecommerce.ecommerceJava.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Solicitud para obtener recomendaciones de productos usando IA")
public class RecommendationRequest {

    @Schema(description = "Consulta del usuario sobre qué tipo de producto busca o necesita",
            example = "Busco algo para cocinar de forma rápida y saludable")
    private String query;

    @Schema(description = "Presupuesto máximo del cliente (opcional)", example = "500.0")
    private Double maxPrice;
}
