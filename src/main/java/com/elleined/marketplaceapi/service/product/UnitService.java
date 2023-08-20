package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.dto.BaseDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.BaseMapper;
import com.elleined.marketplaceapi.model.Unit;
import com.elleined.marketplaceapi.repository.UnitRepository;
import com.elleined.marketplaceapi.service.baseservices.GetService;
import com.elleined.marketplaceapi.service.baseservices.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UnitService implements PostService<Unit, BaseDTO>, GetService<Unit> {
    private final UnitRepository unitRepository;
    private final BaseMapper baseMapper;

    @Override
    public Unit getById(int id) throws ResourceNotFoundException {
        return unitRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unit with id of " + id + " does not exists!"));
    }

    @Override
    public boolean isExists(int id) {
        return unitRepository.existsById(id);
    }

    @Override
    public Unit saveByDTO(BaseDTO baseDTO) {
        Unit unit = baseMapper.toUnitEntity(baseDTO);
        unitRepository.save(unit);
        log.debug("Unit with name of {} saved successfully with id of {}", unit.getName(), unit.getId());
        return unit;
    }

    @Override
    public Unit save(Unit unit) {
        unitRepository.save(unit);
        log.debug("Unit with name of {} saved successfully with id of {}", unit.getName(), unit.getId());
        return unit;
    }
}
