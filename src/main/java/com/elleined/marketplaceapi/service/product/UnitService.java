package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.BaseMapper;
import com.elleined.marketplaceapi.model.Unit;
import com.elleined.marketplaceapi.repository.UnitRepository;
import com.elleined.marketplaceapi.service.BaseEntityService;
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
        return unitRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unit with id of " + id + " does not exists!"));
    }

    @Override
    public boolean existsById(int id) {
        return unitRepository.existsById(id);
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
                .filter(unit -> unit.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Unit with name of " + name + " does not exists!"));
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
