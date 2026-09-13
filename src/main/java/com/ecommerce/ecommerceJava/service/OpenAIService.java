package com.ecommerce.ecommerceJava.service;

import com.ecommerce.ecommerceJava.dto.RecommendationRequest;
import com.ecommerce.ecommerceJava.dto.RecommendationResponse;
import com.ecommerce.ecommerceJava.model.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OpenAIService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.model}")
    private String model;

    public OpenAIService() {
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Solicita a ChatGPT recomendaciones basadas en el catálogo de productos de la BD.
     *
     * @param request  consulta del usuario (qué busca, presupuesto, categoría)
     * @param products todos los productos disponibles en la base de datos
     * @return RecommendationResponse con explicación de la IA y productos recomendados
     */
    public RecommendationResponse getRecommendations(RecommendationRequest request, List<Product> products) {
        if (apiKey == null || apiKey.isBlank()) {
            return new RecommendationResponse(
                    "Servicio de recomendaciones con IA desactivado. Por favor, configura tu OPENAI_API_KEY en el archivo .env para activarlo.",
                    List.of()
            );
        }

        String catalogContext = buildCatalogContext(products);

        String systemPrompt = """
                Eres un asistente experto en recomendaciones de productos para un e-commerce.
                Tu rol es analizar el catálogo de productos disponibles y recomendar los más
                adecuados según la consulta del cliente. Siempre responde en español.
                
                Responde ÚNICAMENTE con un JSON válido con la siguiente estructura exacta:
                {
                  "explanation": "Explicación general de las recomendaciones",
                  "recommendations": [
                    {
                      "productId": <id del producto>,
                      "reason": "Razón específica por la que se recomienda este producto"
                    }
                  ]
                }
                
                Incluye entre 1 y 5 recomendaciones. Si no hay productos relevantes, devuelve
                una lista vacía de recommendations y explica por qué en el campo explanation.
                """;

        String userMessage = buildUserMessage(request, catalogContext);
        String aiResponseContent = callOpenAI(systemPrompt, userMessage);
        return parseAndBuildResponse(aiResponseContent, products);
    }

    private String buildCatalogContext(List<Product> products) {
        if (products.isEmpty()) {
            return "No hay productos disponibles en el catálogo.";
        }
        return products.stream()
                .map(p -> {
                    String base = String.format("ID: %d | Nombre: %s | Descripción: %s | Precio: $%.2f | Stock: %d",
                            p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock());
                    return (p.getCategory() != null && p.getCategory().getNombre() != null)
                            ? base + " | Categoría: " + p.getCategory().getNombre()
                            : base;
                })
                .collect(Collectors.joining("\n"));
    }

    private String buildUserMessage(RecommendationRequest request, String catalogContext) {
        StringBuilder message = new StringBuilder();
        message.append("CATÁLOGO DE PRODUCTOS DISPONIBLES:\n").append(catalogContext).append("\n\n");
        message.append("CONSULTA DEL CLIENTE: ").append(request.getQuery());

        if (request.getMaxPrice() != null) {
            message.append("\nPRESUPUESTO MÁXIMO: $").append(request.getMaxPrice());
        }

        message.append("\n\nBasándote en el catálogo anterior, recomienda los productos más adecuados para este cliente.");
        return message.toString();
    }

    private String callOpenAI(String systemPrompt, String userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", 0.7);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userMessage));
        requestBody.put("messages", messages);

        try {
            String response = restClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            log.error("Error al llamar a la API de OpenAI: {}", e.getMessage());
            throw new RuntimeException("Error al comunicarse con la IA: " + e.getMessage(), e);
        }
    }

    private RecommendationResponse parseAndBuildResponse(String aiContent, List<Product> products) {
        try {
            // El modelo puede envolver el JSON en bloques de código markdown
            String cleanJson = aiContent
                    .replaceAll("(?s)```json", "")
                    .replaceAll("(?s)```", "")
                    .trim();

            JsonNode root = objectMapper.readTree(cleanJson);
            String explanation = root.path("explanation").asText("No se pudo obtener una explicación.");
            JsonNode recommendationsNode = root.path("recommendations");

            Map<Long, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));

            List<RecommendationResponse.RecommendedProduct> recommendedProducts = new ArrayList<>();

            for (JsonNode recNode : recommendationsNode) {
                long productId = recNode.path("productId").asLong(-1);
                String reason = recNode.path("reason").asText("Recomendado por la IA.");

                if (productId != -1 && productMap.containsKey(productId)) {
                    Product p = productMap.get(productId);
                    String catNombre = p.getCategory() != null ? p.getCategory().getNombre() : null;
                    recommendedProducts.add(new RecommendationResponse.RecommendedProduct(
                            p.getId(), p.getName(), p.getDescription(),
                            p.getPrice(), p.getStock(), catNombre, reason));
                }
            }

            return new RecommendationResponse(explanation, recommendedProducts);

        } catch (Exception e) {
            log.error("Error al parsear la respuesta de la IA: {}", e.getMessage());
            return new RecommendationResponse("Respuesta de la IA: " + aiContent, List.of());
        }
    }
}
