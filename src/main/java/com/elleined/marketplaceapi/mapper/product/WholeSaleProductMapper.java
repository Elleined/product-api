package com.elleined.marketplaceapi.mapper.product;

import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.CropService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import com.elleined.marketplaceapi.service.unit.WholeSaleUnitService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
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
    protected WholeSaleProductService wholeSaleProductService;

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
            @Mapping(target = "sellerName", expression = "java(getFullName(wholeSaleProduct.getSeller()))"),
            @Mapping(target = "cropName", source = "wholeSaleProduct.crop.name"),
            @Mapping(target = "shopName", source = "wholeSaleProduct.seller.shop.name"),
            @Mapping(target = "listingDate", expression = "java(wholeSaleProduct.getListingDate().toLocalDate())"),
            @Mapping(target = "unitId", source = "wholeSaleProduct.id"),
            @Mapping(target = "unitName", source = "wholeSaleProduct.name"),
            @Mapping(target = "totalPrice", source = "price")
    })
    public abstract WholeSaleProductDTO toDTO(WholeSaleProduct wholeSaleProduct);

    @Override
    @Mappings({

    })
    public abstract WholeSaleProduct toEntity(WholeSaleProductDTO dto, User seller);

    @Override
    @Mappings({

    })
    public abstract WholeSaleProduct toUpdate(WholeSaleProduct wholeSaleProduct, WholeSaleProductDTO dto);
}
