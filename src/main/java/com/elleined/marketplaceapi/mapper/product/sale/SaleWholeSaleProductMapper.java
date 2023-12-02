package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.SaleWholeSaleRequest;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleWholeSaleProduct;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SaleWholeSaleProductMapper {

    @Mappings({
            @org.mapstruct.Mapping(target = "id", ignore = true),

            @org.mapstruct.Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @org.mapstruct.Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())"),
            @org.mapstruct.Mapping(target = "salePrice", expression = "java(salePrice)"),
            @org.mapstruct.Mapping(target = "wholeSaleProduct", expression = "java(wholeSaleProduct)")
    })
    SaleWholeSaleProduct toEntity(SaleWholeSaleRequest saleWholeSaleRequest,
                                  @Context WholeSaleProduct wholeSaleProduct,
                                  @Context double salePrice);
}
