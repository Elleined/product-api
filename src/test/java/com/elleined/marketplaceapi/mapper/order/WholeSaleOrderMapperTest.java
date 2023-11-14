package com.elleined.marketplaceapi.mapper.order;

import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.elleined.marketplaceapi.model.order.Order.Status.PENDING;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WholeSaleOrderMapperTest {

    @InjectMocks
    private WholeSaleOrderMapper wholeSaleOrderMapper = Mappers.getMapper(WholeSaleOrderMapper.class);

    @Test
    void toEntity() {
        User purchaser = User.builder()
                .id(1)
                .build();

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()
                .id(1)
                .build();

        double expectedPrice = 5_000;
        WholeSaleProduct wholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .id(1)
                .price(new BigDecimal(expectedPrice))
                .build();

        WholeSaleOrderDTO wholeSaleOrderDTO = WholeSaleOrderDTO.wholeSaleOrderDTOBuilder()
                .deliveryAddressId(1)
                .productId(1)
                .build();

        WholeSaleOrder wholeSaleOrder = wholeSaleOrderMapper.toEntity(wholeSaleOrderDTO, purchaser, deliveryAddress, wholeSaleProduct);

        assertEquals(0, wholeSaleOrder.getId());
        assertEquals(expectedPrice, wholeSaleOrder.getPrice());
        assertNotNull(wholeSaleOrder.getOrderDate());

        assertNotNull(wholeSaleOrder.getPurchaser());
        assertEquals(purchaser, wholeSaleOrder.getPurchaser());

        assertNotNull(wholeSaleOrder.getDeliveryAddress());
        assertEquals(wholeSaleOrderDTO.getDeliveryAddressId(), wholeSaleOrder.getDeliveryAddress().getId());

        assertNotNull(wholeSaleOrder.getStatus());
        assertEquals(PENDING, wholeSaleOrder.getStatus());

        assertNull(wholeSaleOrder.getSellerMessage());

        assertNotNull(wholeSaleOrder.getUpdatedAt());

        assertNotNull(wholeSaleOrder.getWholeSaleProduct());
        assertEquals(wholeSaleOrderDTO.getProductId(), wholeSaleOrder.getWholeSaleProduct().getId());
    }

    @Test
    void toDTO() {
    }
}