package com.elleined.marketplaceapi.service.address;

import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.mapper.address.DeliveryAddressMapper;
import com.elleined.marketplaceapi.mapper.address.UserAddressMapper;
import com.elleined.marketplaceapi.mock.AddressMockDataFactory;
import com.elleined.marketplaceapi.mock.UserMockDataFactory;
import com.elleined.marketplaceapi.model.Credential;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserDetails;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.AddressRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;


    @Mock
    private UserAddressMapper userAddressMapper;

    @Mock
    private DeliveryAddressMapper deliveryAddressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    @DisplayName("Saving user address")
    void saveUserAddress() {
        User registeringUser = new User();
        AddressDTO addressDTO = new AddressDTO();

        UserAddress expected = UserAddress.userAddressBuilder()
                .id(1)
                .regionName("Region name")
                .provinceName("Province name")
                .cityName("City name")
                .baranggayName("Baranggay name")
                .build();

        when(userAddressMapper.toEntity(addressDTO,  registeringUser)).thenReturn(expected);
        when(addressRepository.save(expected)).thenReturn(expected);
        addressService.saveUserAddress(registeringUser, addressDTO);

        verify(addressRepository).save(expected);
        assertNotNull(registeringUser.getAddress());
        assertNotNull(expected.getRegionName());
        assertNotNull(expected.getProvinceName());
        assertNotNull(expected.getCityName());
        assertNotNull(expected.getBaranggayName());
    }

    @Test
    void saveDeliveryAddress() {
    }

    @Test
    void getAllDeliveryAddress() {
    }

    @Test
    void getDeliveryAddressById() {
    }
}