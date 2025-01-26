package com.shared.basecrud.repositories;

import java.util.List;

import com.shared.basecrud.dtos.tables.BaseTableRowDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

public abstract class BaseRepository<TableRow extends BaseTableRowDto> {

    @PersistenceContext
    private EntityManager entityManager;

    private final Class<TableRow> tableClass;

    protected BaseRepository(Class<TableRow> tableClass) {
        this.tableClass = tableClass;
    }

    public List<TableRow> findAll() {
        String query = "SELECT t FROM " + tableClass.getSimpleName() + " t";
        TypedQuery<TableRow> typedQuery = entityManager.createQuery(query, tableClass);
        return typedQuery.getResultList();
    }

    public TableRow findById(String id) {
        return entityManager.find(tableClass, id);
    }

    public TableRow save(TableRow entity) {
        entityManager.persist(entity);
        return entity;
    }

    public TableRow update(TableRow entity) {
        return entityManager.merge(entity);
    }

    public void delete(String id) {
    	TableRow entity = findById(id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }
}
