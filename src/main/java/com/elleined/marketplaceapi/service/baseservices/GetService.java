package com.elleined.marketplaceapi.service.baseservices;

import com.elleined.marketplaceapi.exception.ResourceNotFoundException;

public interface GetService<ENTITY> {
    ENTITY getById(int id) throws ResourceNotFoundException;
    boolean isExists(int id);
    boolean isExists(ENTITY entity);
}
