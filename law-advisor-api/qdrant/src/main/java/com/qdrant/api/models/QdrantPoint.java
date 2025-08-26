package com.qdrant.api.models;

import java.util.List;
import java.util.Map;

public class QdrantPoint {
    private int id;
    private List<Float> vector;
    private Map<String, Object> payload;
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Float> getVector() {
		return vector;
	}
	public void setVector(List<Float> vector) {
		this.vector = vector;
	}
	public Map<String, Object> getPayload() {
		return payload;
	}
	public void setPayload(Map<String, Object> payload) {
		this.payload = payload;
	}   
}

