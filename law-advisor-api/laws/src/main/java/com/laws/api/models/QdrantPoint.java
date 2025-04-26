package com.laws.api.models;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QdrantPoint {
    private int id;
    private List<Float> vector;
    private Map<String, Object> payload;
}

