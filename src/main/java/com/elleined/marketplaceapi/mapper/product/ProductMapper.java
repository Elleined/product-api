package com.elleined.marketplaceapi.mapper.product;

import com.elleined.marketplaceapi.dto.product.ProductDTO;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.CropService;
import com.elleined.marketplaceapi.service.product.ProductService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public interface ProductMapper<DTO extends ProductDTO, ENTITY extends Product> {

    DTO toDTO(ENTITY entity);
    ENTITY toEntity(DTO dto, User seller);

    ENTITY toUpdate(ENTITY entity, DTO dto);

    default String getFullName(User user) {
        return user.getUserDetails().getFirstName() + " " + user.getUserDetails().getMiddleName() + " " + user.getUserDetails().getLastName();
    }
}
