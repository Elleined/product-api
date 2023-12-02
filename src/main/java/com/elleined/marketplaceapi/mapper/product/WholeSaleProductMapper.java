package com.elleined.marketplaceapi.mapper.product;

import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.unit.WholeSaleUnit;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        imports = {Product.State.class,
                Product.Status.class}
)
public interface WholeSaleProductMapper {

    @Mappings({
            @Mapping(target = "state", source = "state"),
            @Mapping(target = "sellerId", source = "seller.id"),
            @Mapping(target = "sellerName", expression = "java(wholeSaleProduct.getSeller().getFullName())"),
            @Mapping(target = "cropName", source = "crop.name"),
            @Mapping(target = "shopName", source = "seller.shop.name"),
            @Mapping(target = "listingDate", expression = "java(wholeSaleProduct.getListingDate().toLocalDate())"),
            @Mapping(target = "unitId", source = "wholeSaleUnit.id"),
            @Mapping(target = "unitName", source = "wholeSaleUnit.name"),
            @Mapping(target = "totalPrice", source = "price")
    })
    WholeSaleProductDTO toDTO(WholeSaleProduct wholeSaleProduct);

    @Mappings({
            @Mapping(target = "id", ignore = true),

            @Mapping(target = "picture", expression = "java(picture)"),
            @Mapping(target = "listingDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "state", expression = "java(State.PENDING)"),
            @Mapping(target = "status", expression = "java(Status.ACTIVE)"),
            @Mapping(target = "crop", expression = "java(crop)"),
            @Mapping(target = "wholeSaleUnit", expression = "java(wholeSaleUnit)"),
            @Mapping(target = "seller", expression = "java(seller)"),
            @Mapping(target = "price", source = "totalPrice"),

            @Mapping(target = "privateChatRooms", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "wholeSaleCartItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "wholeSaleOrders", expression = "java(new java.util.ArrayList<>())"),
    })
    WholeSaleProduct toEntity(WholeSaleProductDTO dto,
                              @Context User seller,
                              @Context Crop crop,
                              @Context WholeSaleUnit wholeSaleUnit,
                              @Context String picture);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "listingDate", ignore = true),
            @Mapping(target = "state", ignore = true),
            @Mapping(target = "seller", ignore = true),
            @Mapping(target = "privateChatRooms", ignore = true),
            @Mapping(target = "wholeSaleCartItems", ignore = true),
            @Mapping(target = "wholeSaleOrders", ignore = true),

            @Mapping(target = "picture", expression = "java(picture)"),
            @Mapping(target = "wholeSaleUnit", expression = "java(wholeSaleUnit)"),
            @Mapping(target = "crop", expression = "java(crop)"),
            @Mapping(target = "price", source = "totalPrice")
    })
    WholeSaleProduct toUpdate(@MappingTarget WholeSaleProduct wholeSaleProduct,
                              WholeSaleProductDTO dto,
                              @Context Crop crop,
                              @Context WholeSaleUnit wholeSaleUnit,
                              @Context String picture);
}
