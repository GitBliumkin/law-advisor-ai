package com.laws.api.qdrant;

import java.util.List;

import org.springframework.web.reactive.function.client.WebClient;

import com.laws.api.models.QdrantPoint;
import com.laws.api.models.QdrantSearchRequest;
import com.laws.api.models.QdrantUpsertRequest;

import reactor.core.publisher.Mono;

public class QdrantService {
	private final WebClient webClient;

    public QdrantService(WebClient qdrantWebClient) {
        this.webClient = qdrantWebClient;
    }

    public Mono<String> upsertVector(String collectionName, QdrantPoint point) {
        QdrantUpsertRequest request = new QdrantUpsertRequest(List.of(point));

        return webClient.post()
                .uri("/collections/{collection}/points", collectionName)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> searchSimilar(String collectionName, List<Float> queryVector, int limit) {
        QdrantSearchRequest searchRequest = new QdrantSearchRequest(queryVector, limit);

        return webClient.post()
                .uri("/collections/{collection}/points/search", collectionName)
                .bodyValue(searchRequest)
                .retrieve()
                .bodyToMono(String.class);
    }
}
