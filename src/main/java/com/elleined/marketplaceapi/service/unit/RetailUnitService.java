package com.elleined.marketplaceapi.service.unit;

import com.elleined.marketplaceapi.dto.UnitDTO;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.unit.RetailUnitMapper;
import com.elleined.marketplaceapi.model.unit.RetailUnit;
import com.elleined.marketplaceapi.model.unit.Unit;
import com.elleined.marketplaceapi.repository.unit.RetailUnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
@Primary
public class RetailUnitService implements UnitService {
    private final RetailUnitRepository retailUnitRepository;
    private final RetailUnitMapper retailUnitMapper;

    @Override
    public RetailUnit save(String name) {
        RetailUnit retailUnit = retailUnitMapper.toEntity(name);
        retailUnitRepository.save(retailUnit);
        log.debug("Retail unit with name of {} saved successfully", name);
        return retailUnit;
    }

    @Override
    public RetailUnit getById(int id) throws ResourceNotFoundException {
        return retailUnitRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Retail unit with id of " + id + " doesn't exists!"));
    }

    @Override
    public List<RetailUnit> getAll() {
        return retailUnitRepository.findAll().stream()
                .sorted(Comparator.comparing(Unit::getName))
                .toList();
    }

}
