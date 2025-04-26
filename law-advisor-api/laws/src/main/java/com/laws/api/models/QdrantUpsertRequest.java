package com.laws.api.models;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QdrantUpsertRequest {
    private List<QdrantPoint> points;
}

