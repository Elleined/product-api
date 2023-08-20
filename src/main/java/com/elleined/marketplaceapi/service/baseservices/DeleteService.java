package com.elleined.marketplaceapi.service.baseservices;

public interface DeleteService<ENTITY> {
    void delete(ENTITY entity);
    void delete(int id);
}
