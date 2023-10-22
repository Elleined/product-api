package com.elleined.marketplaceapi.service.unit;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.unit.Unit;

import java.util.List;

public interface UnitService {
    Unit save(String name);

    Unit getById(int id) throws ResourceNotFoundException;
    List<String> getAll();
}
