package com.laws.api.repositories;

import org.springframework.stereotype.Repository;

import com.laws.api.tables.LawUnitsAuditTable;
import com.shared.basecrud.repositories.BaseRepository;

@Repository
public interface LawUnitsAuditRepository extends BaseRepository<LawUnitsAuditTable, String> {}
