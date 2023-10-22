package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;

import java.util.List;

// This interface is used in crop, unit, and suffix implementation services
public interface BaseEntityService<T> {
    T save(String name);

    T getById(int id) throws ResourceNotFoundException;
    List<String> getAll();

    boolean existsByName(String name) throws ResourceNotFoundException;


    T getByName(String name) throws ResourceNotFoundException;
}
