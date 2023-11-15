package com.elleined.marketplaceapi.mapper.cart;

import com.elleined.marketplaceapi.dto.cart.RetailCartItemDTO;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.elleined.marketplaceapi.model.order.Order.Status.PENDING;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RetailCartItemMapperTest {

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
        RetailCartItem expected = RetailCartItem.retailCartItemBuilder()
                .id(1)
                .price(200)
                .createdAt(LocalDateTime.now())
                .purchaser(User.builder()
                        .id(1)
                        .build())
                .deliveryAddress(DeliveryAddress.deliveryAddressBuilder()
                        .id(1)
                        .build())
                .retailProduct(RetailProduct.retailProductBuilder()
                        .id(1)
                        .build())
                .orderQuantity(5)
                .build();

        RetailCartItemDTO actual = retailCartItemMapper.toDTO(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getPurchaser().getId(), actual.getPurchaserId());
        assertEquals(expected.getDeliveryAddress().getId(), actual.getDeliveryAddressId());
        assertEquals(expected.getRetailProduct().getId(), actual.getProductId());
        assertEquals(expected.getOrderQuantity(), actual.getOrderQuantity());
    }

    @Test
    void cartItemToOrder() {
        RetailCartItem actual = RetailCartItem.retailCartItemBuilder()
                .id(1)
                .price(200)
                .createdAt(LocalDateTime.now())
                .purchaser(User.builder()
                        .id(1)
                        .build())
                .deliveryAddress(DeliveryAddress.deliveryAddressBuilder()
                        .id(1)
                        .build())
                .retailProduct(RetailProduct.retailProductBuilder()
                        .id(1)
                        .build())
                .orderQuantity(5)
                .build();

        RetailOrder expected = retailCartItemMapper.cartItemToOrder(actual);

        assertEquals(0, expected.getId());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getPurchaser(), actual.getPurchaser());
        assertEquals(expected.getRetailProduct(), actual.getRetailProduct());
        assertEquals(expected.getDeliveryAddress(), actual.getDeliveryAddress());

        assertNotNull(expected.getOrderDate());
        assertNotNull(expected.getUpdatedAt());
        assertNull(expected.getSellerMessage());
        assertEquals(PENDING, expected.getStatus());

    }
}
