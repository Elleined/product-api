package com.elleined.marketplaceapi.mapper.address;

import com.elleined.marketplaceapi.dto.address.DeliveryAddressDTO;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DeliveryAddressMapperTest {

    @InjectMocks
    private DeliveryAddressMapper deliveryAddressMapper = Mappers.getMapper(DeliveryAddressMapper.class);

    @Test
    void toEntity() {
        User user = new User();

        DeliveryAddressDTO deliveryAddressDTO = DeliveryAddressDTO.deliveryAddressBuilder()
                .title("Title")
                .details("House Details")
                .regionName("Region Name")
                .provinceName("Province Name")
                .baranggayName("Baranggay Name")
                .build();

        DeliveryAddress deliveryAddress = deliveryAddressMapper.toEntity(deliveryAddressDTO, user);

        assertEquals(0, deliveryAddress.getId());
        assertNotNull(deliveryAddress.getTitle());
        assertNotNull(deliveryAddress.getDetails());
        assertNotNull(deliveryAddress.getRegionName());
        assertNotNull(deliveryAddress.getProvinceName());
        assertNotNull(deliveryAddress.getBaranggayName());

        assertNotNull(deliveryAddress.getWholeSaleCartItems());
        assertNotNull(deliveryAddress.getRetailCartItems());
        assertNotNull(deliveryAddress.getWholeSaleOrders());
        assertNotNull(deliveryAddress.getRetailOrders());

        assertEquals(user, deliveryAddress.getUser());
    }

    @Test
    void toDTO() {
        User user = new User();
        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()
                .id(1)
                .title("Title")
                .details("House Details")
                .regionName("Region Name")
                .provinceName("Province Name")
                .baranggayName("Baranggay Name")
                .retailCartItems(new ArrayList<>())
                .wholeSaleCartItems(new ArrayList<>())
                .retailOrders(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .build();

        DeliveryAddressDTO deliveryAddressDTO = deliveryAddressMapper.toDTO(deliveryAddress);

        assertEquals(1, deliveryAddressDTO.getId());
        assertNotNull(deliveryAddressDTO.getTitle());
        assertNotNull(deliveryAddressDTO.getDetails());
        assertNotNull(deliveryAddressDTO.getRegionName());
        assertNotNull(deliveryAddressDTO.getProvinceName());
        assertNotNull(deliveryAddressDTO.getBaranggayName());
    }
}