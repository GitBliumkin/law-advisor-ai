package com.laws.api.qdrant;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.laws.api.models.QdrantPoint;
import com.laws.api.models.QdrantSearchRequest;
import com.laws.api.models.QdrantUpsertRequest;

import reactor.core.publisher.Mono;

@Service
public class QdrantService {

    private final WebClient webClient;

    public QdrantService(WebClient qdrantWebClient) {
        this.webClient = qdrantWebClient;
    }

    // 1. Upsert a vector
    public Mono<String> upsertVector(String collectionName, QdrantPoint point) {
        QdrantUpsertRequest request = new QdrantUpsertRequest();
        request.setPoints(List.of(point));

        return webClient.post()
                .uri("/collections/{collection}/points", collectionName)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class);
    }

    // 2. Search similar vectors
    public Mono<String> searchSimilar(String collectionName, List<Float> queryVector, Integer limit) {
        QdrantSearchRequest searchRequest = new QdrantSearchRequest();
        searchRequest.setLimit(limit);
        searchRequest.setVector(queryVector);

        return webClient.post()
                .uri("/collections/{collection}/points/search", collectionName)
                .bodyValue(searchRequest)
                .retrieve()
                .bodyToMono(String.class);
    }

    // 3. Ping Qdrant
    public Mono<String> ping() {
        return webClient.get()
                .uri("/collections")
                .retrieve()
                .bodyToMono(String.class);
    }

    // 4. Create a collection
    public Mono<String> createCollection(String name, Integer size, String distance) {
        Map<String, Object> vectors = Map.of(
            "size", size,
            "distance", distance
        );

        Map<String, Object> request = Map.of("vectors", vectors);

        return webClient.put()
                .uri("/collections/{collection}", name)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class);
    }

    // 5. Delete a vector by ID
    public Mono<String> deleteVector(String collectionName, Integer id) {
        Map<String, Object> request = Map.of("points", List.of(id));

        return webClient.post()
                .uri("/collections/{collection}/points/delete", collectionName)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class);
    }
}
