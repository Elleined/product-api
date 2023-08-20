package com.elleined.marketplaceapi.service.baseservices;

import com.elleined.marketplaceapi.exception.ResourceNotFoundException;

public interface GetService<T> {
    T getById(int id) throws ResourceNotFoundException;
    boolean isExists(int id);
}
