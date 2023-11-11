package com.elleined.marketplaceapi.mapper.product;

import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.CropService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import com.elleined.marketplaceapi.service.unit.WholeSaleUnitService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring",
        imports = {Product.State.class,
                Product.Status.class,
                Product.SaleStatus.class}
)
public abstract class WholeSaleProductMapper implements ProductMapper<WholeSaleProductDTO, WholeSaleProduct> {

    @Lazy
    @Autowired
    protected WholeSaleUnitService wholeSaleUnitService;

    @Lazy
    @Autowired
    protected CropService cropService;

    @Override
    @Mappings({
            @Mapping(target = "state", source = "wholeSaleProduct.state"),
            @Mapping(target = "sellerId", source = "wholeSaleProduct.seller.id"),
            @Mapping(target = "sellerName", expression = "java(wholeSaleProduct.getSeller().getFullName())"),
            @Mapping(target = "cropName", source = "wholeSaleProduct.crop.name"),
            @Mapping(target = "shopName", source = "wholeSaleProduct.seller.shop.name"),
            @Mapping(target = "listingDate", expression = "java(wholeSaleProduct.getListingDate().toLocalDate())"),
            @Mapping(target = "unitId", source = "wholeSaleProduct.wholeSaleUnit.id"),
            @Mapping(target = "unitName", source = "wholeSaleProduct.wholeSaleUnit.name"),
            @Mapping(target = "totalPrice", source = "price")
    })
    public abstract WholeSaleProductDTO toDTO(WholeSaleProduct wholeSaleProduct);

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "picture", ignore = true),

            @Mapping(target = "listingDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "state", expression = "java(State.PENDING)"),
            @Mapping(target = "saleStatus", expression = "java(SaleStatus.NOT_ON_SALE)"),
            @Mapping(target = "status", expression = "java(Status.ACTIVE)"),
            @Mapping(target = "crop", expression = "java(cropService.getByName(dto.getCropName()))"),
            @Mapping(target = "wholeSaleUnit", expression = "java(wholeSaleUnitService.getById(dto.getUnitId()))"),
            @Mapping(target = "seller", expression = "java(seller)"),
            @Mapping(target = "price", source = "totalPrice"),

            @Mapping(target = "privateChatRooms", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "wholeSaleCartItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "wholeSaleOrders", expression = "java(new java.util.ArrayList<>())"),
    })
    public abstract WholeSaleProduct toEntity(WholeSaleProductDTO dto, @Context User seller);

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "listingDate", ignore = true),
            @Mapping(target = "state", ignore = true),
            @Mapping(target = "seller", ignore = true),
            @Mapping(target = "privateChatRooms", ignore = true),
            @Mapping(target = "wholeSaleCartItems", ignore = true),
            @Mapping(target = "wholeSaleOrders", ignore = true),
            @Mapping(target = "picture", ignore = true),
            @Mapping(target = "saleStatus", ignore = true),

            @Mapping(target = "wholeSaleUnit", expression = "java(wholeSaleUnitService.getById(dto.getUnitId()))"),
            @Mapping(target = "crop", expression = "java(cropService.getByName(dto.getCropName()))"),
            @Mapping(target = "price", source = "dto.totalPrice")
    })
    public abstract WholeSaleProduct toUpdate(@MappingTarget WholeSaleProduct wholeSaleProduct, WholeSaleProductDTO dto);
}
