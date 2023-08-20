package com.elleined.marketplaceapi.service.baseservices;

import com.elleined.marketplaceapi.exception.ResourceNotFoundException;

public interface PutService<ENTITY, DTO> {
    void update(ENTITY entity, DTO dto) throws ResourceNotFoundException;
}
