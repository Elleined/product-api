package com.elleined.marketplaceapi.mapper.product;

import com.elleined.marketplaceapi.dto.product.ProductDTO;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;

public interface ProductMapper<DTO extends ProductDTO, ENTITY extends Product> {

    DTO toDTO(ENTITY entity);
    ENTITY toEntity(DTO dto, User seller);

    ENTITY toUpdate(ENTITY entity, DTO dto);
}
