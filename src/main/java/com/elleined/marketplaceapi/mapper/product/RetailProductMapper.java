package com.elleined.marketplaceapi.mapper.product;

import com.elleined.marketplaceapi.model.product.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", imports = {Product.State.class, Product.Status.class})
public class RetailProductMapper {
    
}
