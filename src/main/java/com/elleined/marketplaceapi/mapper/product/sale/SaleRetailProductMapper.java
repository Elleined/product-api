package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.response.SaleRetailProductResponse;
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

            @Mapping(target = "quantityPerUnit", expression = "java(quantityPerUnit)"),
            @Mapping(target = "pricePerUnit", expression = "java(pricePerUnit)"),
            @Mapping(target = "retailProduct", expression = "java(retailProduct)"),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    })
    SaleRetailProduct toEntity(RetailProduct retailProduct,
                               @Context int quantityPerUnit,
                               @Context Integer pricePerUnit);


    @Mappings({
            @Mapping(target = "salePrice", expression = "java(salePrice)"),
            @Mapping(target = "salePercentage", expression = "java(salePercentage)")
    })
    SaleRetailProductResponse toDTO(SaleRetailProduct saleRetailProduct,
                                    @Context int salePercentage,
                                    @Context double salePrice);
}
