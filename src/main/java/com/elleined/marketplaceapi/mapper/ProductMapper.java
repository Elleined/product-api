package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.CropService;
import com.elleined.marketplaceapi.service.product.ProductService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", imports = {Product.State.class, Product.Status.class})
public abstract class ProductMapper {

    @Autowired @Lazy
    protected CropService cropService;

    @Autowired @Lazy
    protected UnitService unitService;

    @Autowired @Lazy
    protected ProductService productService;

    @Mappings({
            @Mapping(target = "state", source = "product.state"),
            @Mapping(target = "sellerId", source = "product.seller.id"),
            @Mapping(target = "sellerName", expression = "java(getFullName(product.getSeller()))"),
            @Mapping(target = "cropName", source = "product.crop.name"),
            @Mapping(target = "shopName", source = "product.seller.shop.name"),
            @Mapping(target = "totalPrice", expression = "java(productService.calculateTotalPrice(product.getPricePerUnit(), product.getQuantityPerUnit(), product.getAvailableQuantity()))"),
            @Mapping(target = "listingDate", expression = "java(product.getListingDate().toLocalDate())")
    })
    public abstract ProductDTO toDTO(Product product);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "picture", ignore = true),

            @Mapping(target = "listingDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "state", expression = "java(State.PENDING)"),
            @Mapping(target = "status", expression = "java(Status.ACTIVE)"),
            @Mapping(target = "crop", expression = "java(cropService.getByName(productDTO.getCropName()))"),
            @Mapping(target = "seller", expression = "java(seller)"),

            @Mapping(target = "addedToCarts", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "orders", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "privateChatRooms", expression = "java(new java.util.ArrayList<>())")
    })
    public abstract Product toEntity(ProductDTO productDTO, @Context User seller) throws ResourceNotFoundException;

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "addedToCarts", ignore = true),
            @Mapping(target = "orders", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "listingDate", ignore = true),
            @Mapping(target = "state", ignore = true),
            @Mapping(target = "seller", ignore = true),
            @Mapping(target = "privateChatRooms", ignore = true),

            @Mapping(target = "crop", expression = "java(cropService.getByName(productDTO.getCropName()))"),

    })
    public abstract Product toUpdate(@MappingTarget Product product, ProductDTO productDTO);

    final protected String getFullName(User user) {
        return user.getUserDetails().getFirstName() + " " + user.getUserDetails().getMiddleName() + " " + user.getUserDetails().getLastName();
    }
}
