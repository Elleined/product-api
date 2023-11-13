package com.elleined.marketplaceapi.mapper.cart;

import com.elleined.marketplaceapi.dto.cart.RetailCartItemDTO;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetailCartItemMapperTest {

    @Spy
    protected RetailProductService retailProductService;

    @Spy
    protected AddressService addressService;

    @InjectMocks
    private RetailCartItemMapper retailCartItemMapper = Mappers.getMapper(RetailCartItemMapper.class);

    @Test
    void toEntity() {
        User user = new User();
        user.setDeliveryAddresses(new ArrayList<>());

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()
                .id(1)
                .build();
        user.getDeliveryAddresses().add(deliveryAddress);

        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .id(1)
                .availableQuantity(5_000)
                .build();

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .deliveryAddressId(1)
                .orderQuantity(20)
                .build();

        when(addressService.getDeliveryAddressById(user, retailCartItemDTO.getDeliveryAddressId())).thenReturn(deliveryAddress);
        when(retailProductService.getById(retailCartItemDTO.getProductId())).thenReturn(retailProduct);

        RetailCartItem retailCartItem = retailCartItemMapper.toEntity(retailCartItemDTO, user);

        assertEquals(0, retailCartItem.getId());
        assertNotNull(retailCartItem.getCreatedAt());
        assertNotNull(retailCartItem.getPrice());
        assertNotNull(retailCartItem.getRetailProduct());

        assertEquals(user, retailCartItem.getPurchaser());
    }

    @Test
    void toDTO() {
    }

    @Test
    void cartItemToOrder() {
    }
}