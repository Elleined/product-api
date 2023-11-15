package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.mapper.address.DeliveryAddressMapper;
import com.elleined.marketplaceapi.mapper.address.UserAddressMapper;
import com.elleined.marketplaceapi.model.Credential;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserDetails;
import com.elleined.marketplaceapi.model.user.UserDetails.Suffix;
import com.elleined.marketplaceapi.model.user.UserVerification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import static com.elleined.marketplaceapi.model.user.UserDetails.Gender.MALE;
import static com.elleined.marketplaceapi.model.user.UserVerification.Status;
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
        assertEquals(Status.NOT_VERIFIED, actual.getUserVerification().getStatus());
        assertNull(actual.getUserVerification().getValidId());

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
        User expected = User.builder()
                .id(1)
                .referralCode("Referral Code")
                .balance(new BigDecimal(5_000))
                .userVerification(UserVerification.builder()
                        .validId("Valid id")
                        .status(Status.NOT_VERIFIED)
                        .build())
                .userCredential(Credential.builder()
                        .email("email")
                        .password("password")
                        .build())
                .userDetails(UserDetails.builder()
                        .firstName("First name")
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender(MALE)
                        .birthDate(LocalDate.now())
                        .mobileNumber("09999999999")
                        .picture("picutre")
                        .suffix(Suffix.NONE)
                        .registrationDate(LocalDateTime.now())
                        .build())
                .address(UserAddress.userAddressBuilder()
                        .id(1)
                        .regionName("Region name")
                        .provinceName("Province name")
                        .cityName("City name")
                        .baranggayName("Baranggay name")
                        .build())
                .shop(Shop.builder()
                        .id(1)
                        .name("Name")
                        .description("Description")
                        .picture("PIcture")
                        .build())
                .premium(Premium.builder()
                        .registrationDate(LocalDateTime.now())
                        .build())
                .referredUsers(new HashSet<>())
                .deliveryAddresses(new ArrayList<>())
                .retailOrders(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .retailCartItems(new ArrayList<>())
                .wholeSaleCartItems(new ArrayList<>())
                .retailProducts(new ArrayList<>())
                .wholeSaleProducts(new ArrayList<>())
                .sentMoneyTransactions(new ArrayList<>())
                .receiveMoneyTransactions(new ArrayList<>())
                .withdrawTransactions(new ArrayList<>())
                .depositTransactions(new ArrayList<>())
                .privateChatMessages(new ArrayList<>())
                .createdChatRooms(new ArrayList<>())
                .participatingChatRooms(new ArrayList<>())
                .build();
        
        UserDTO actual = userMapper.toDTO(expected);
        
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getReferralCode(), actual.getReferralCode());

        assertEquals(expected.getUserVerification().getStatus().name(), actual.getStatus());
        assertEquals(expected.getUserVerification().getValidId(), actual.getValidId());

        // premium
        assertTrue(actual.isPremium());

        assertNull(actual.getInvitationReferralCode());

        assertNotNull(actual.getBalance());
        assertEquals(expected.getBalance(), actual.getBalance());

        assertNotNull(actual.getUserDetailsDTO());
        assertNotNull(actual.getAddressDTO());
        assertNotNull(actual.getUserCredentialDTO());
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
        assertEquals(expected.getGender(), actual.getGender().name());
        assertEquals(expected.getBirthDate(), actual.getBirthDate());
        assertEquals(expected.getMobileNumber(), actual.getMobileNumber());
        assertEquals(expected.getPicture(), actual.getPicture());
        assertEquals(expected.getSuffix(), actual.getSuffix().name());

        assertNotNull(actual.getFirstName());
        assertNotNull(actual.getMiddleName());
        assertNotNull(actual.getLastName());
        assertNotNull(actual.getRegistrationDate());
        assertNotNull(actual.getGender().name());
        assertNotNull(actual.getBirthDate());
        assertNotNull(actual.getMobileNumber());
        assertNotNull(actual.getPicture());
        assertNotNull(actual.getSuffix().name());
    }

    @Test
    void toUserDetailsDTO() {
        UserDetails expected = UserDetails.builder()
                .firstName("First name")
                .middleName("MIddle name")
                .lastName("Last name")
                .gender(MALE)
                .birthDate(LocalDate.now())
                .mobileNumber("09999999999")
                .picture("picutre")
                .suffix(Suffix.NONE)
                .registrationDate(LocalDateTime.now())
                .build();

        UserDTO.UserDetailsDTO actual = userMapper.toUserDetailsDTO(expected);

        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getMiddleName(), actual.getMiddleName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getGender().name(), actual.getGender());
        assertEquals(expected.getBirthDate(), actual.getBirthDate());
        assertEquals(expected.getMobileNumber(), actual.getMobileNumber());
        assertEquals(expected.getPicture(), actual.getPicture());
        assertEquals(expected.getSuffix().name(), actual.getSuffix());

        assertNotNull(actual.getFirstName());
        assertNotNull(actual.getMiddleName());
        assertNotNull(actual.getLastName());
        assertNotNull(actual.getRegistrationDate());
        assertNotNull(actual.getGender());
        assertNotNull(actual.getBirthDate());
        assertNotNull(actual.getMobileNumber());
        assertNotNull(actual.getPicture());
        assertNotNull(actual.getSuffix());
    }
}