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

    @Spy
    private UserAddressMapper userAddressMapper;

    @Mock
    private DeliveryAddressMapper deliveryAddressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void saveUserAddress() {
        User registeringUser = UserMockDataFactory.getUser();
        AddressDTO addressDTO = AddressMockDataFactory.getAddress();

        UserAddress userAddress = userAddressMapper.toEntity(addressDTO, registeringUser);

        when(addressRepository.save(userAddress)).thenReturn(userAddress);

        verify(addressRepository).save(userAddress);
        assertNotNull(registeringUser.getAddress());
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