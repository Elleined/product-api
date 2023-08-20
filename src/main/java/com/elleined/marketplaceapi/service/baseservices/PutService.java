package com.elleined.marketplaceapi.service.baseservices;

import com.elleined.marketplaceapi.exception.ResourceNotFoundException;

public interface PutService<DTO> {
    void update(int id, DTO dto) throws ResourceNotFoundException;
}
