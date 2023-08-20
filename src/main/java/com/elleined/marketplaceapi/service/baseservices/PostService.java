package com.elleined.marketplaceapi.service.baseservices;

public interface PostService<ENTITY, DTO> {
    ENTITY saveByDTO(DTO dto);
    ENTITY save(ENTITY entity);
}
