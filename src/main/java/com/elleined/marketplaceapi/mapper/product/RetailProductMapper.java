package com.elleined.marketplaceapi.mapper.product;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.CropService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.service.unit.RetailUnitService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring",
        imports = {Product.State.class,
                Product.Status.class,
                Product.SaleStatus.class}
)
public abstract class RetailProductMapper implements ProductMapper<RetailProductDTO, RetailProduct> {

    @Autowired
    @Lazy
    protected RetailProductService retailProductService;

    @Autowired
    @Lazy
    protected RetailUnitService retailUnitService;

    @Autowired
    @Lazy
    protected CropService cropService;

    @Override
    @Mappings({
            @Mapping(target = "state", source = "retailProduct.state"),
            @Mapping(target = "sellerId", source = "retailProduct.seller.id"),
            @Mapping(target = "sellerName", expression = "java(retailProduct.getSeller().getFullName())"),
            @Mapping(target = "cropName", source = "retailProduct.crop.name"),
            @Mapping(target = "shopName", source = "retailProduct.seller.shop.name"),
            @Mapping(target = "totalPrice", expression = "java(retailProductService.calculateTotalPrice(retailProduct.getPricePerUnit(), retailProduct.getQuantityPerUnit(), retailProduct.getQuantityPerUnit()))"),
            @Mapping(target = "listingDate", expression = "java(retailProduct.getListingDate().toLocalDate())"),
            @Mapping(target = "unitId", source = "retailUnit.id"),
            @Mapping(target = "unitName", source = "retailUnit.name")
    })
    public abstract RetailProductDTO toDTO(RetailProduct retailProduct);

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "picture", ignore = true),

            @Mapping(target = "listingDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "state", expression = "java(State.PENDING)"),
            @Mapping(target = "saleStatus", expression = "java(SaleStatus.NOT_ON_SALE)"),
            @Mapping(target = "status", expression = "java(Status.ACTIVE)"),
            @Mapping(target = "crop", expression = "java(cropService.getByName(retailProductDTO.getCropName()))"),
            @Mapping(target = "retailUnit", expression = "java(retailUnitService.getById(retailProductDTO.getUnitId()))"),
            @Mapping(target = "seller", expression = "java(seller)"),

            @Mapping(target = "privateChatRooms", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "retailCartItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "retailOrders", expression = "java(new java.util.ArrayList<>())"),
    })
    public abstract RetailProduct toEntity(RetailProductDTO retailProductDTO, @Context User seller);

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "listingDate", ignore = true),
            @Mapping(target = "state", ignore = true),
            @Mapping(target = "seller", ignore = true),
            @Mapping(target = "privateChatRooms", ignore = true),
            @Mapping(target = "retailCartItems", ignore = true),
            @Mapping(target = "retailOrders", ignore = true),
            @Mapping(target = "picture", ignore = true),

            @Mapping(target = "saleStatus", expression = "java(retailProduct.getSaleStatus())"),
            @Mapping(target = "retailUnit", expression = "java(retailUnitService.getById(dto.getUnitId()))"),
            @Mapping(target = "crop", expression = "java(cropService.getByName(dto.getCropName()))"),
    })
    public abstract RetailProduct toUpdate(@MappingTarget RetailProduct retailProduct, RetailProductDTO dto);
}
