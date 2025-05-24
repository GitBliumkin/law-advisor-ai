package com.laws.api.models;


public class QdrantCollectionRequest {
    private String name;
    private Integer size;
    private String distance;
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}  
}
			