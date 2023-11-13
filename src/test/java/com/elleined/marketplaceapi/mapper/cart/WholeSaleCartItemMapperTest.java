package com.elleined.marketplaceapi.mapper.cart;

import com.elleined.marketplaceapi.dto.cart.WholeSaleCartItemDTO;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class WholeSaleCartItemMapperTest {

    @InjectMocks
    private WholeSaleCartItemMapper wholeSaleCartItemMapper = Mappers.getMapper(WholeSaleCartItemMapper.class);

    @Test
    void toEntity() {
        User user = new User();

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()
                .id(1)
                .build();

        double expectedPrice = 5000.00;
        WholeSaleProduct wholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .id(1)
                .price(new BigDecimal(expectedPrice))
                .build();

        WholeSaleCartItemDTO actual = WholeSaleCartItemDTO.wholeSaleCartItemDTOBuilder()
                .productId(1)
                .deliveryAddressId(1)
                .build();

        WholeSaleCartItem expected = wholeSaleCartItemMapper.toEntity(actual, user, deliveryAddress, wholeSaleProduct);
        int actualId = expected.getId();
        double actualPrice = expected.getPrice();

        assertEquals(0, actualId);
        assertEquals(expectedPrice, actualPrice);
        assertNotNull(expected.getCreatedAt());

        assertEquals(expected.getPurchaser(), user);
        assertEquals(expected.getPurchaser().getId(), actual.getPurchaserId());

        assertEquals(expected.getDeliveryAddress(), deliveryAddress);
        assertEquals(expected.getDeliveryAddress().getId(), actual.getDeliveryAddressId());

        assertEquals(expected.getWholeSaleProduct(), wholeSaleProduct);
        assertEquals(expected.getWholeSaleProduct().getId(), actual.getProductId());
    }

    @Test
    void toDTO() {
    }

    @Test
    void cartItemToOrder() {
    }
}