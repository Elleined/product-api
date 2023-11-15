package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.mapper.address.DeliveryAddressMapper;
import com.elleined.marketplaceapi.mapper.address.UserAddressMapper;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.elleined.marketplaceapi.model.user.UserDetails.Gender.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {


    @Spy
    private DeliveryAddressMapper deliveryAddressMapper = Mappers.getMapper(DeliveryAddressMapper.class);

    @Spy
    private UserAddressMapper userAddressMapper = Mappers.getMapper(UserAddressMapper.class);

    @Spy
    private CredentialMapper credentialMapper = Mappers.getMapper(CredentialMapper.class);

    @InjectMocks
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);


    @Test
    void toEntity() {

        UserDTO expected = UserDTO.builder()
                .userDetailsDTO(UserDTO.UserDetailsDTO.builder()
                        .firstName("First name")
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender("MALE")
                        .birthDate(LocalDate.now())
                        .mobileNumber("09999999999")
                        .picture("picutre")
                        .suffix("NONE")
                        .build())
                .addressDTO(AddressDTO.builder()
                        .details("detauk")
                        .regionName("region name")
                        .provinceName("province name")
                        .cityName("city name")
                        .baranggayName("barnaggay name")
                        .build())
                .userCredentialDTO(CredentialDTO.builder()
                        .email("email")
                        .password("password")
                        .build())
                .invitationReferralCode("")
                .build();

        User actual = userMapper.toEntity(expected);

        assertEquals(0, actual.getId());
        assertNotNull(actual.getReferralCode());
        assertNotNull(actual.getBalance());

        assertNotNull(actual.getUserVerification());
        assertNotNull(actual.getUserCredential());
        assertNotNull(actual.getUserDetails());

        assertNull(actual.getAddress());
        assertNull(actual.getShop());
        assertNull(actual.getPremium());

        assertNotNull(actual.getReferredUsers());

        assertNotNull(actual.getDeliveryAddresses());

        assertNotNull(actual.getRetailOrders());
        assertNotNull(actual.getWholeSaleOrders());

        assertNotNull(actual.getRetailCartItems());
        assertNotNull(actual.getWholeSaleCartItems());

        assertNotNull(actual.getRetailProducts());
        assertNotNull(actual.getWholeSaleProducts());

        assertNotNull(actual.getSentMoneyTransactions());
        assertNotNull(actual.getReceiveMoneyTransactions());
        assertNotNull(actual.getWithdrawTransactions());
        assertNotNull(actual.getDepositTransactions());

        assertNotNull(actual.getPrivateChatMessages());
        assertNotNull(actual.getCreatedChatRooms());
        assertNotNull(actual.getParticipatingChatRooms());
    }

    @Test
    void toDTO() {
    }

    @Test
    void toUserDetailsEntity() {
        UserDTO.UserDetailsDTO expected = UserDTO.UserDetailsDTO.builder()
                .firstName("First name")
                .middleName("MIddle name")
                .lastName("Last name")
                .gender("MALE")
                .birthDate(LocalDate.now())
                .mobileNumber("09999999999")
                .picture("picutre")
                .suffix("NONE")
                .build();

        UserDetails actual = userMapper.toUserDetailsEntity(expected);

        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getMiddleName(), actual.getMiddleName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertNotNull(actual.getRegistrationDate());
        assertEquals(expected.getGender(), actual.getGender().name());
        assertEquals(expected.getBirthDate(), actual.getBirthDate());
        assertEquals(expected.getMobileNumber(), actual.getMobileNumber());
        assertEquals(expected.getPicture(), actual.getPicture());
        assertEquals(expected.getSuffix(), actual.getSuffix().name());

        // equals and not null
    }

    @Test
    void toUserDetailsDTO() {
    }
}