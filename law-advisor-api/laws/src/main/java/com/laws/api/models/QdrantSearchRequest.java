package com.laws.api.models;

import java.util.List;

public class QdrantSearchRequest {
    private List<Float> vector;
    private Integer limit;
    
    public List<Float> getVector() {
    	return this.vector;
    }
    
    public void setVector(List<Float> vector) {
    	this.vector = vector;
    }
    
    public Integer getLimit() {
    	return this.limit;
    }
    
    public void setLimit(Integer limit) {
    	this.limit = limit;
    }
    
}
