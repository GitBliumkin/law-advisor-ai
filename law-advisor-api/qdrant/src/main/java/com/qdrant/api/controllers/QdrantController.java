package com.qdrant.api.controllers;

import com.shared.basecrud.dtos.responses.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.qdrant.api.models.QdrantPoint;
import com.qdrant.api.services.QdrantService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/qdrant")
public class QdrantController {

  private static final Logger logger = LoggerFactory.getLogger(QdrantController.class);

  @Value("${spring.application.name:service}")
  private String serviceName;

  private final QdrantService qdrantService;

  public QdrantController(QdrantService qdrantService) {
    this.qdrantService = qdrantService;
  }

  // --- 1) Create (or update) a collection -----------------------------------

  public static final class CreateCollectionRequest {
    @NotBlank public String name;
    @NotNull @Positive public Integer size;
    /**
     * e.g., "Cosine", "Dot", or "Euclid" depending on your Qdrant setup
     */
    @NotBlank public String distance;
  }

  @PutMapping("/collections")
  public BaseResponse<String> createCollection(@Valid @RequestBody CreateCollectionRequest req) {
    logger.info("QdrantController.createCollection name={} size={} distance={}", req.name, req.size, req.distance);
    String body = qdrantService.createCollection(req.name, req.size, req.distance).block();
    return BaseResponse.success(serviceName, body);
  }

  // --- 2) Upsert a point into a collection ----------------------------------

  public static final class UpsertVectorRequest {
    @NotBlank public String collectionName;
    @NotNull public QdrantPoint point;
  }

  @PostMapping("/points")
  public BaseResponse<String> upsertPoint(@Valid @RequestBody UpsertVectorRequest req) {
    logger.info("QdrantController.upsertPoint collection={}", req.collectionName);
    String body = qdrantService.upsertVector(req.collectionName, req.point).block();
    return BaseResponse.success(serviceName, body);
  }

  // --- 3) Search similar vectors --------------------------------------------

  public static final class SearchRequest {
    @NotBlank public String collectionName;
    @NotNull @Size(min = 1) public List<@NotNull Float> vector;
    @NotNull @Positive public Integer limit;
  }

  @PostMapping("/search")
  public BaseResponse<String> search(@Valid @RequestBody SearchRequest req) {
    logger.info("QdrantController.search collection={} limit={}", req.collectionName, req.limit);
    String body = qdrantService.searchSimilar(req.collectionName, req.vector, req.limit).block();
    return BaseResponse.success(serviceName, body);
  }

  // --- 4) Delete a point by ID ----------------------------------------------

  public static final class DeletePointRequest {
    @NotBlank public String collectionName;
    @NotNull public Integer id;
  }

  @DeleteMapping("/points")
  public BaseResponse<String> deletePoint(@Valid @RequestBody DeletePointRequest req) {
    logger.info("QdrantController.deletePoint collection={} id={}", req.collectionName, req.id);
    String body = qdrantService.deleteVector(req.collectionName, req.id).block();
    return BaseResponse.success(serviceName, body);
  }

  // --- (Optional) Convenience: path-style variants --------------------------
  // If you prefer RESTier URLs without bodies for simple calls, keep these too.

  @PutMapping("/collections/{name}")
  public BaseResponse<String> createCollectionPath(
      @PathVariable String name,
      @RequestParam @Positive Integer size,
      @RequestParam String distance) {
    logger.info("QdrantController.createCollectionPath name={} size={} distance={}", name, size, distance);
    String body = qdrantService.createCollection(name, size, distance).block();
    return BaseResponse.success(serviceName, body);
  }

  @DeleteMapping("/collections/{collection}/points/{id}")
  public BaseResponse<String> deletePointPath(@PathVariable String collection, @PathVariable Integer id) {
    logger.info("QdrantController.deletePointPath collection={} id={}", collection, id);
    String body = qdrantService.deleteVector(collection, id).block();
    return BaseResponse.success(serviceName, body);
  }

  @PostMapping("/collections/{collection}/search")
  public BaseResponse<String> searchPath(
      @PathVariable String collection,
      @RequestBody Map<String, Object> body) {
    // accepts: {"vector":[...], "limit":10}
    @SuppressWarnings("unchecked")
    List<Float> vector = (List<Float>) body.get("vector");
    Integer limit = (Integer) body.getOrDefault("limit", 10);
    logger.info("QdrantController.searchPath collection={} limit={}", collection, limit);
    String resp = qdrantService.searchSimilar(collection, vector, limit).block();
    return BaseResponse.success(serviceName, resp);
  }
}
