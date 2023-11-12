package com.elleined.marketplaceapi.service.address;

import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.dto.address.DeliveryAddressDTO;
import com.elleined.marketplaceapi.exception.user.DeliveryAddressLimitException;
import com.elleined.marketplaceapi.mapper.address.DeliveryAddressMapper;
import com.elleined.marketplaceapi.mapper.address.UserAddressMapper;
import com.elleined.marketplaceapi.mock.AddressMockDataFactory;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.AddressRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        User orderingUser = User.builder()
                .deliveryAddresses(new ArrayList<>())
                .build();

        DeliveryAddressDTO deliveryAddressDTO = new DeliveryAddressDTO();

        DeliveryAddress mockAddress = AddressMockDataFactory.getDeliveryAddress(1);

        when(deliveryAddressMapper.toEntity(deliveryAddressDTO, orderingUser)).thenReturn(mockAddress);
        when(addressRepository.save(mockAddress)).thenReturn(mockAddress);
        DeliveryAddress savedDeliveryAddress = addressService.saveDeliveryAddress(orderingUser, deliveryAddressDTO);

        verify(addressRepository).save(savedDeliveryAddress);
        assertNotNull(orderingUser.getDeliveryAddresses());
        assertNotNull(mockAddress.getRegionName());
        assertNotNull(mockAddress.getProvinceName());
        assertNotNull(mockAddress.getCityName());
        assertNotNull(mockAddress.getBaranggayName());
        assertFalse(orderingUser.getDeliveryAddresses().isEmpty());
    }

    @Test
    void shouldThrowDeliveryAddressLimitException() {
        User orderingUser = User.builder()
                .deliveryAddresses(new ArrayList<>())
                .build();

        DeliveryAddressDTO deliveryAddressDTO = new DeliveryAddressDTO();

        DeliveryAddress mockAddress = AddressMockDataFactory.getDeliveryAddress(1);

        orderingUser.getDeliveryAddresses().add(mockAddress);
        orderingUser.getDeliveryAddresses().add(mockAddress);
        orderingUser.getDeliveryAddresses().add(mockAddress);
        orderingUser.getDeliveryAddresses().add(mockAddress);
        orderingUser.getDeliveryAddresses().add(mockAddress);

        verifyNoInteractions(addressRepository, deliveryAddressMapper);
        assertThrows(DeliveryAddressLimitException.class, () -> addressService.saveDeliveryAddress(orderingUser, deliveryAddressDTO));
    }

    @Test
    void getAllDeliveryAddress() {
        User user = User.builder()
                .deliveryAddresses(new ArrayList<>())
                .build();

        assertNotNull(user.getDeliveryAddresses());
        assertDoesNotThrow(user::getDeliveryAddresses);
    }

    @Test
    void getDeliveryAddressById() {
        User user = new User();

        List<DeliveryAddress> deliveryAddresses = Arrays.asList(
                AddressMockDataFactory.getDeliveryAddress(1),
                AddressMockDataFactory.getDeliveryAddress(2)
        );
        user.setDeliveryAddresses(deliveryAddresses);
        assertDoesNotThrow(() -> addressService.getDeliveryAddressById(user, 1));
    }
}