package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.mapper.address.DeliveryAddressMapper;
import com.elleined.marketplaceapi.mapper.address.UserAddressMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {


    @Spy
    private DeliveryAddressMapper deliveryAddressMapperDeliveryAddressMapper = Mappers.getMapper(DeliveryAddressMapper.class);

    @Spy
    private UserAddressMapper userAddressMapperUserAddressMapper = Mappers.getMapper(UserAddressMapper.class);

    @Spy
    private CredentialMapper credentialMapperCredentialMapper = Mappers.getMapper(CredentialMapper.class);

    @InjectMocks
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);


    @Test
    void toEntity() {
    }

    @Test
    void toDTO() {
    }

    @Test
    void toUserDetailsEntity() {
    }

    @Test
    void toUserDetailsDTO() {
    }
}