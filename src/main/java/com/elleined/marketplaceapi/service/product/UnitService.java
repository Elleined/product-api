package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.dto.BaseDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
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


    @Override
    public Unit getById(int id) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public boolean isExists(int id) {
        return false;
    }

    @Override
    public Unit saveByDTO(BaseDTO baseDTO) {
        return null;
    }

    @Override
    public Unit save(Unit unit) {
        return null;
    }
}
