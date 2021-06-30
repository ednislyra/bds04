package com.devsuperior.bds04.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds04.dto.CityDTO;
import com.devsuperior.bds04.entities.City;
import com.devsuperior.bds04.repositories.CityRepository;
import com.devsuperior.bds04.service.exception.DatabaseException;
import com.devsuperior.bds04.service.exception.ResourceNotFoundException;

@Service
public class CityService {

	@Autowired
	private CityRepository repository;

	public List<CityDTO> findAll(){
		List<City> list = repository.findAll(Sort.by("name"));
		return list.stream().map(x -> new CityDTO(x)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = false)
    public CityDTO insert(CityDTO dto) {
        City city = new City();
        city.setName(dto.getName());
        city = repository.save(city);
        return new CityDTO(city);
    }

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity Violation!");
		}
	}
}