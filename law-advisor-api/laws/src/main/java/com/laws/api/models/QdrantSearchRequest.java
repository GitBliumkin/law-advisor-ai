package com.laws.api.models;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QdrantSearchRequest {
    private List<Float> vector;
    private int limit;
}
