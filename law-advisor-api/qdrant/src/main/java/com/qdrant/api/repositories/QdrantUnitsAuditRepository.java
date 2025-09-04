package com.qdrant.api.repositories;

import org.springframework.stereotype.Repository;

import com.qdrant.api.tables.QdrantUnitAuditTable;
import com.shared.basecrud.repositories.BaseRepository;

@Repository
public interface QdrantUnitsAuditRepository extends BaseRepository<QdrantUnitAuditTable, String> {}
