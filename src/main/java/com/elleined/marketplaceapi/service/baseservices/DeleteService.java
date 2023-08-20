package com.elleined.marketplaceapi.service.baseservices;

import com.elleined.marketplaceapi.exception.ResourceNotFoundException;

public interface DeleteService<ENTITY> {
    void delete(int id) throws ResourceNotFoundException;
}
