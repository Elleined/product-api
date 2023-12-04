package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.response.SaleWholeSaleResponse;
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
            @Mapping(target = "wholeSaleProduct", expression = "java(wholeSaleProduct)"),
            @Mapping(target = "salePrice", expression = "java(salePrice)")
    })
    SaleWholeSaleProduct toEntity(WholeSaleProduct wholeSaleProduct,
                                  @Context double salePrice);

    SaleWholeSaleResponse toDTO(SaleWholeSaleProduct saleWholeSaleProduct, int salePercentage);
}
