package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.response.SaleWholeSaleResponse;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleWholeSaleProduct;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring")
public abstract class SaleWholeSaleProductMapper {
    @Autowired
    @Lazy
    protected WholeSaleProductService wholeSaleProductService;
    @Mappings({
            @Mapping(target = "id", ignore = true),

            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "wholeSaleProduct", expression = "java(wholeSaleProduct)"),
            @Mapping(target = "salePrice", expression = "java(salePrice)")
    })
    public abstract SaleWholeSaleProduct toEntity(WholeSaleProduct wholeSaleProduct,
                                  @Context double salePrice);

    @Mapping(target = "salePercentage", expression = "java(getSalePercentage(saleWholeSaleProduct))")
    public abstract SaleWholeSaleResponse toDTO(SaleWholeSaleProduct saleWholeSaleProduct);

    public int getSalePercentage(SaleWholeSaleProduct saleWholeSaleProduct) {
        return wholeSaleProductService.getSalePercentage(saleWholeSaleProduct.getWholeSaleProduct().getPrice().doubleValue(), saleWholeSaleProduct.getSalePrice());
    }
}
