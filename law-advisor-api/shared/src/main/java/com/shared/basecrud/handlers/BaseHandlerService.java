package com.shared.basecrud.handlers;

import java.util.List;

import com.shared.basecrud.dtos.tables.BaseTableRowDto;
import com.shared.basecrud.repositories.BaseRepository;

public class BaseHandlerService<TableRow extends BaseTableRowDto> {
		
	 	private final BaseRepository<TableRow> repository;

	    protected BaseHandlerService(BaseRepository<TableRow> repository) {
	        this.repository = repository;
	    }


	    public List<TableRow> getAll() {
	        List<TableRow> objcets = repository.findAll();
	        return objcets;
	    }

	    public TableRow getById(String id) {
	    	TableRow object = repository.findById(id);
	        return object;
	    }

	    public TableRow create(TableRow dto) {
	    	TableRow savedObject = repository.save(dto);
	        return savedObject;
	    }

	    public TableRow update(TableRow dto) {
	    	TableRow updatedObject = repository.update(dto);
	        return updatedObject;
	    }

	    public void delete(String id) {
	    	repository.delete(id);
	    }
}
