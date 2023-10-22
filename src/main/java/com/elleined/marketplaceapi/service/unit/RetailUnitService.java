package com.elleined.marketplaceapi.service.unit;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.unit.Unit;
import com.elleined.marketplaceapi.repository.unit.RetailUnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
@Primary
public class RetailUnitService implements UnitService {
    private final RetailUnitRepository retailUnitRepository;

    @Override
    public Unit save(String name) {
        return null;
    }

    @Override
    public Unit getById(int id) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public List<String> getAll() {
        return null;
    }
}
