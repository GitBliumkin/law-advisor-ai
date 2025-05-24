package com.laws.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laws.api.handlers.LawUnitsAuditHandler;
import com.laws.api.models.QdrantCollectionRequest;
import com.laws.api.models.QdrantPoint;
import com.laws.api.models.QdrantSearchRequest;
import com.laws.api.qdrant.QdrantService;
import com.laws.api.tables.LawUnitsAuditTable;
import com.shared.basecrud.controllers.BaseController;
import com.shared.models.dtos.LawUnitsAuditDto;
import com.shared.models.requests.LawUnitsAuditRequest;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/law-units-audit")
public class LawUnitsAuditController extends BaseController<LawUnitsAuditRequest, LawUnitsAuditDto, LawUnitsAuditTable> {
     @Autowired
     QdrantService qdrantService;
	
	  @Autowired
	  protected LawUnitsAuditController(LawUnitsAuditHandler handler) {
	    super(LawUnitsAuditController.class, handler);
	  }
	  
	  @PostMapping("/post-vector")
	  public Mono<String> upsertVector(@RequestBody QdrantPoint point) {
	     return qdrantService.upsertVector("my_vectors", point);
	  }

	    // 4. Search similar vectors
	  @PostMapping("/search")
	  public Mono<String> searchVector(@RequestBody QdrantSearchRequest request) {
	        return qdrantService.searchSimilar("my_vectors", request.getVector(), request.getLimit());
	  }
	  
	  @GetMapping("/ping")
	    public Mono<String> pingQdrant() {
	        return qdrantService.ping();
	    }

	    // 2. Create collection
	    @PutMapping("/collection")
	    public Mono<String> createCollection(@RequestBody(required = false) QdrantCollectionRequest request) {
	        return qdrantService.createCollection(
	            request != null ? request.getName() : "my_vectors",
	            request != null ? request.getSize() : 1536,
	            request != null ? request.getDistance() : "Cosine"
	        );
	    }
	    
	    @DeleteMapping("/{id}")
	    public Mono<String> deleteVector(@PathVariable Integer id) {
	        return qdrantService.deleteVector("my_vectors", id);
	    }

}