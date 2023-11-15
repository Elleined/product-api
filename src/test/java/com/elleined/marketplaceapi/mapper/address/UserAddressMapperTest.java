package com.elleined.marketplaceapi.mapper.address;

import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UserAddressMapperTest {

    @InjectMocks
    private UserAddressMapper userAddressMapper = Mappers.getMapper(UserAddressMapper.class);

    @Test
    void toEntity() {
        User user = new User();
        AddressDTO addressDTO = AddressDTO.builder()
                .details("House Details")
                .regionName("Region Name")
                .provinceName("Province Name")
                .cityName("City Name")
                .baranggayName("Baranggay Name")
                .build();

        UserAddress userAddress = userAddressMapper.toEntity(addressDTO, user);

        assertEquals(0, userAddress.getId());
        assertNotNull(userAddress.getDetails());
        assertNotNull(userAddress.getRegionName());
        assertNotNull(userAddress.getProvinceName());
        assertNotNull(userAddress.getCityName());
        assertNotNull(userAddress.getBaranggayName());

        assertEquals(user, userAddress.getUser());
    }

    @Test
    void toDTO() {
        UserAddress userAddress = UserAddress.userAddressBuilder()
                .id(1)
                .details("House Details")
                .regionName("Region Name")
                .provinceName("Province Name")
                .cityName("City Name")
                .baranggayName("Baranggay Name")
                .build();

        AddressDTO addressDTO = userAddressMapper.toDTO(userAddress);

        assertEquals(1,  addressDTO.getId());
        assertNotNull(addressDTO.getDetails());
        assertNotNull(addressDTO.getRegionName());
        assertNotNull(addressDTO.getProvinceName());
        assertNotNull(addressDTO.getCityName());
        assertNotNull(addressDTO.getBaranggayName());
    }
}