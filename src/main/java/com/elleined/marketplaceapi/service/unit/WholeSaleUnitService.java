package com.elleined.marketplaceapi.service.unit;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.unit.WholeSaleUnitMapper;
import com.elleined.marketplaceapi.model.unit.RetailUnit;
import com.elleined.marketplaceapi.model.unit.Unit;
import com.elleined.marketplaceapi.model.unit.WholeSaleUnit;
import com.elleined.marketplaceapi.repository.unit.WholeSaleUnitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
@Qualifier("wholeSaleUnitService")
public class WholeSaleUnitService implements UnitService {
    private final WholeSaleUnitRepository wholeSaleUnitRepository;
    private final WholeSaleUnitMapper wholeSaleUnitMapper;

    @Override
    public WholeSaleUnit save(String name) {
        WholeSaleUnit wholeSaleUnit = wholeSaleUnitMapper.toEntity(name);
        wholeSaleUnitRepository.save(wholeSaleUnit);
        log.debug("Whole sale unit with name of {} saved successfully", name);
        return wholeSaleUnit;
    }

    @Override
    public WholeSaleUnit getById(int id) throws ResourceNotFoundException {
        return wholeSaleUnitRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Whole sale unit with id of " + id + " doesn't exists!"));
    }

    @Override
    public List<WholeSaleUnit> getAll() {
        return wholeSaleUnitRepository.findAll().stream()
                .sorted(Comparator.comparing(Unit::getName))
                .toList();
    }
}
