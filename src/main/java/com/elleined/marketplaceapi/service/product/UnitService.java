package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.BaseMapper;
import com.elleined.marketplaceapi.model.unit.Unit;
import com.elleined.marketplaceapi.repository.unit.UnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UnitService implements BaseEntityService<Unit> {
    private final UnitRepository unitRepository;
    private final BaseMapper baseMapper;

    @Override
    public Unit getById(int id) throws ResourceNotFoundException {
        return unitRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unit does not exists!"));
    }

    @Override
    public List<String> getAll() {
        return unitRepository.findAll().stream()
                .map(Unit::getName)
                .sorted()
                .toList();
    }

    @Override
    public Unit getByName(String name) throws ResourceNotFoundException {
        return unitRepository.findAll().stream()
                .filter(unit -> unit.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Unit does not exists!"));
    }

    @Override
    public boolean existsByName(String name) {
        return unitRepository.findAll().stream()
                .map(Unit::getName)
                .anyMatch(name::equalsIgnoreCase);
    }

    @Override
    public Unit save(String name) {
        Unit unit = baseMapper.toUnitEntity(name);
        unitRepository.save(unit);
        log.debug("Unit with name of {} saved successfully with id of {}", unit.getName(), unit.getId());
        return unit;
    }

}
