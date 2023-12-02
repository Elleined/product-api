package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.SaleRetailProductRequest;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleRetailProduct;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SaleRetailProductMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),

            @Mapping(target = "retailProduct", expression = "java(retailProduct)"),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    })
    SaleRetailProduct toEntity(SaleRetailProductRequest saleRetailProductRequest,
                               @Context RetailProduct retailProduct);
}
