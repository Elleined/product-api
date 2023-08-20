package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.service.product.CropService;
import com.elleined.marketplaceapi.service.product.UnitService;
import com.elleined.marketplaceapi.service.user.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", imports = {Product.State.class, Product.Status.class})
public abstract class ProductMapper {

    @Autowired @Lazy
    protected CropService cropService;

    @Autowired @Lazy
    protected UnitService unitService;

    @Autowired @Lazy
    protected UserService userService;

    @Mappings({
            @Mapping(target = "state", source = "product.state"),
            @Mapping(target = "status", source = "product.status"),

            @Mapping(target = "sellerId", source = "product.seller.id"),
            @Mapping(target = "sellerName", source = "product.seller.userDetails.firstName"),
            @Mapping(target = "cropName", source = "product.crop.name"),
            @Mapping(target = "unitName", source = "product.unit.name"),
            @Mapping(target = "shopName", source = "product.seller.shop.name")
    })
    public abstract ProductDTO toDTO(Product product);

    @Mappings({
            @Mapping(target = "id", ignore = true),

            @Mapping(target = "listingDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "state", expression = "java(State.PENDING)"),
            @Mapping(target = "status", expression = "java(Status.ACTIVE)"),
            @Mapping(target = "crop", expression = "java(cropService.getByName(productDTO.getCropName()))"),
            @Mapping(target = "unit", expression = "java(unitService.getByName(productDTO.getUnitName()))"),
            @Mapping(target = "seller", expression = "java(userService.getById(productDTO.getSellerId()))"), // seller alias for currentUser

            @Mapping(target = "cartItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "orderItems", expression = "java(new java.util.ArrayList<>())")
    })
    public abstract Product toEntity(ProductDTO productDTO) throws ResourceNotFoundException;

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "cartItems", ignore = true),
            @Mapping(target = "orderItems", ignore = true),
            @Mapping(target = "state", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "listingDate", ignore = true),
            @Mapping(target = "seller", ignore = true),

            @Mapping(target = "crop", expression = "java(cropService.getByName(productDTO.getCropName()))"),
            @Mapping(target = "unit", expression = "java(unitService.getByName(productDTO.getUnitName()))"),

    })
    public abstract Product toUpdate(@MappingTarget Product product, ProductDTO productDTO);
}
