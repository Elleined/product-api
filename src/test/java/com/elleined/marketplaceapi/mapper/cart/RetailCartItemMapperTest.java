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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        int expectedOrderQuantity = 200;
        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .deliveryAddressId(1)
                .orderQuantity(expectedOrderQuantity)
                .build();
        double expectedPrice = 10_000.00;

        RetailCartItem retailCartItem = retailCartItemMapper.toEntity(retailCartItemDTO, user, deliveryAddress, expectedPrice, retailProduct);
        int actualId = retailCartItem.getId();
        double actualPrice = retailCartItem.getPrice();
        int actualOrderQuantity = retailCartItem.getOrderQuantity();

        assertEquals(0, actualId);
        assertEquals(expectedPrice, actualPrice);
        assertNotNull(retailCartItem.getCreatedAt());
        assertNotNull(retailCartItem.getPurchaser());
        assertNotNull(retailCartItem.getDeliveryAddress());
        assertNotNull(retailCartItem.getRetailProduct());
        assertEquals(expectedOrderQuantity, actualOrderQuantity);

        assertEquals(deliveryAddress, retailCartItem.getDeliveryAddress());
        assertEquals(user, retailCartItem.getPurchaser());
    }

    @Test
    void toDTO() {
        
    }

    @Test
    void cartItemToOrder() {
    }
}