package com.laws.api.models;

import java.util.List;

public class QdrantUpsertRequest {
    private List<QdrantPoint> points;
    
    public void setPoints(List<QdrantPoint> points) {
    	this.points = points;
    }
    
    public List<QdrantPoint> getPoints(){
    	return this.points;
    }
}

