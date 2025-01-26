package com.shared.basecrud.repositories;

import com.shared.basecrud.tables.BaseTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<Table extends BaseTable> extends JpaRepository<Table, String> {}
