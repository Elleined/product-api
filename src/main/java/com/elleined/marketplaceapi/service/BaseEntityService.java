package com.elleined.marketplaceapi.service;

import com.elleined.marketplaceapi.exception.ResourceNotFoundException;

import java.util.List;

// This interface is used in crop, unit, and suffix implementation services
public interface BaseEntityService<T> {
    T save(String name);

    T getById(int id) throws ResourceNotFoundException;
    boolean existsById(int id);

    boolean existsByName(String name) throws ResourceNotFoundException;

    List<String> getAll();

    T getByName(String name) throws ResourceNotFoundException;
}
