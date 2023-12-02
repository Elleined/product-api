package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.request.SaleWholeSaleRequest;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleWholeSaleProduct;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SaleWholeSaleProductMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),

            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "wholeSaleProduct", expression = "java(wholeSaleProduct)")
    })
    SaleWholeSaleProduct toEntity(SaleWholeSaleRequest saleWholeSaleRequest,
                                  @Context WholeSaleProduct wholeSaleProduct);

    SaleWholeSaleRequest toDTO(SaleWholeSaleProduct saleWholeSaleProduct);
}
