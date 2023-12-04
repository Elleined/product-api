package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.response.SaleRetailProductResponse;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleRetailProduct;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring")
public abstract class SaleRetailProductMapper {

    @Autowired
    @Lazy
    protected RetailProductService retailProductService;

    @Mappings({
            @Mapping(target = "id", ignore = true),

            @Mapping(target = "quantityPerUnit", expression = "java(quantityPerUnit)"),
            @Mapping(target = "pricePerUnit", expression = "java(pricePerUnit)"),
            @Mapping(target = "retailProduct", expression = "java(retailProduct)"),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    })
    public abstract SaleRetailProduct toEntity(RetailProduct retailProduct,
                               @Context int quantityPerUnit,
                               @Context Integer pricePerUnit);


    @Mappings({
            @Mapping(target = "salePrice", expression = "java(retailProductService.calculateTotalPrice(saleRetailProduct.getPricePerUnit(), saleRetailProduct.getQuantityPerUnit(), saleRetailProduct.getRetailProduct().getAvailableQuantity()))"),
            @Mapping(target = "salePercentage", expression = "java(getSalePercentage(saleRetailProduct))")
    })
    public abstract SaleRetailProductResponse toDTO(SaleRetailProduct saleRetailProduct);

    protected int getSalePercentage(SaleRetailProduct saleRetailProduct) {
        double totalPrice = retailProductService.calculateTotalPrice(saleRetailProduct.getRetailProduct());
        double salePrice = retailProductService.calculateTotalPrice(saleRetailProduct.getPricePerUnit(), saleRetailProduct.getQuantityPerUnit(), saleRetailProduct.getRetailProduct().getAvailableQuantity());
        return retailProductService.getSalePercentage(totalPrice, salePrice);
    }
}
