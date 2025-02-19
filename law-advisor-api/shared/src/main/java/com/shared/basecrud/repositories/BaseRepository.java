package com.shared.basecrud.repositories;

import com.shared.basecrud.tables.BaseTable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface BaseRepository<Table extends BaseTable, ID> extends JpaRepository<Table, ID> {

  @Query("SELECT e FROM #{#entityName} e WHERE e.deletedOn IS NULL")
  List<Table> findAllActive();

  @Query("SELECT e FROM #{#entityName} e WHERE e.deletedOn IS NULL")
  Page<Table> findAllActive(Pageable pageable);

  @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.deletedOn IS NULL")
  Optional<Table> findByIdActive(@Param("id") ID id);
}
