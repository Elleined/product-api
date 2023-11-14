package com.elleined.marketplaceapi.mapper.order;

import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class RetailOrderMapperTest {

    @InjectMocks
    private RetailOrderMapper retailOrderMapper = Mappers.getMapper(RetailOrderMapper.class);

    @Test
    void toEntity() {
        User purchaser = User.builder()
                .id(1)
                .build();

        User seller = User.builder()
                .id(2)
                .build();

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()
                .id(1)
                .build();

        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .id(1)
                .availableQuantity(5_000)
                .seller(seller)
                .build();

        double expectedPrice = 10_000;
        int expectedOrderQuantity = 10;
        RetailOrderDTO retailOrderDTO = RetailOrderDTO.retailOrderDTOBuilder()
                .deliveryAddressId(1)
                .productId(1)
                .orderQuantity(expectedOrderQuantity)
                .purchaserId(1)
                .build();

        RetailOrder retailOrder = retailOrderMapper.toEntity(retailOrderDTO, purchaser, deliveryAddress, expectedPrice, retailProduct);

        assertEquals(0, retailOrder.getId());
        assertEquals(expectedPrice, retailOrder.getPrice());
        assertNotNull(retailOrder.getOrderDate());

        assertNotNull(retailOrder.getPurchaser());
        assertEquals(retailOrder.getPurchaser().getId(), retailOrderDTO.getPurchaserId());

        assertNotNull(retailOrder.getDeliveryAddress());
        assertEquals(retailOrder.getDeliveryAddress().getId(), retailOrderDTO.getDeliveryAddressId());

        assertEquals(Order.Status.PENDING, retailOrder.getStatus());
        assertNull(retailOrder.getSellerMessage());
        assertNotNull(retailOrder.getUpdatedAt());

        assertNotNull(retailOrder.getRetailProduct());
        assertEquals(retailOrder.getRetailProduct().getId(), retailOrderDTO.getProductId());

        assertEquals(expectedOrderQuantity, retailOrder.getOrderQuantity());
    }

    @Test
    void toDTO() {
        User purchaser = User.builder()
                .id(1)
                .build();

        User seller = User.builder()
                .id(2)
                .build();
        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .seller(seller)
                .id(1)
                .availableQuantity(10_000)
                .build();

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()
                .id(1)
                .build();

        double expectedPrice = 10_000;
        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .id(1)
                .price(expectedPrice)
                .orderDate(LocalDateTime.now())
                .purchaser(purchaser)
                .deliveryAddress(deliveryAddress)
                .status(Order.Status.PENDING)
                .sellerMessage(null)
                .updatedAt(LocalDateTime.now())
                .retailProduct(retailProduct)
                .orderQuantity(500)
                .build();

        RetailOrderDTO retailOrderDTO = retailOrderMapper.toDTO(retailOrder);

        assertEquals(1, retailOrderDTO.getId());
        assertEquals(expectedPrice, retailOrderDTO.getPrice());
        assertEquals(retailOrder.getRetailProduct().getSeller().getId(), retailOrderDTO.getSellerId());
        assertNotNull(retailOrderDTO.getOrderDate());
        assertEquals(retailOrder.getRetailProduct().getId(), retailOrderDTO.getProductId());
        assertEquals(retailOrder.getPurchaser().getId(), retailOrderDTO.getPurchaserId());
        assertEquals(retailOrder.getDeliveryAddress().getId(), retailOrderDTO.getDeliveryAddressId());
        assertNull(retailOrderDTO.getSellerMessage());
        assertNotNull(retailOrderDTO.getUpdatedAt());
        assertEquals(retailOrder.getOrderQuantity(), retailOrderDTO.getOrderQuantity());
    }
}