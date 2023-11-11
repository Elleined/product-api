package com.elleined.marketplaceapi.mock;

import com.elleined.marketplaceapi.model.Credential;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserDetails;
import com.elleined.marketplaceapi.model.user.UserVerification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public interface UserMockDataFactory {

    static User getUser() {
        return User.builder()
                .id(1)
                .premium(Premium.builder()
                        .id(1)
                        .registrationDate(LocalDateTime.now())
                        .build())
                .shop(Shop.builder()
                        .id(1)
                        .name("ShopName")
                        .description("Shop Description")
                        .picture("Shop Picture")
                        .build())
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.NOT_VERIFIED)
                        .build())
                .userCredential(Credential.builder()
                        .email("UserEmail@gmail.com")
                        .password("userPassword")
                        .build())
                .referralCode( UUID.randomUUID().toString() )
                .deliveryAddresses( new ArrayList<>() )
                .referredUsers( new HashSet<>() )
                .balance( new BigDecimal(0) )
                .userDetails(UserDetails.builder()
                        .firstName("FirstName")
                        .middleName("MiddleName")
                        .lastName("LastName")
                        .gender(UserDetails.Gender.MALE)
                        .birthDate(LocalDate.now())
                        .mobileNumber("099999999999")
                        .picture("myPicture.jpg")
                        .suffix(UserDetails.Suffix.NONE)
                        .registrationDate(LocalDateTime.now())
                        .build())
                .depositTransactions( new ArrayList<>() )
                .receiveMoneyTransactions( new ArrayList<>() )
                .sentMoneyTransactions( new ArrayList<>() )
                .withdrawTransactions( new ArrayList<>() )
                .privateChatMessages( new ArrayList<>() )
                .createdChatRooms( new ArrayList<>() )
                .participatingChatRooms( new ArrayList<>() )
                .retailCartItems( new ArrayList<>() )
                .wholeSaleCartItems( new ArrayList<>() )
                .retailOrders( new ArrayList<>() )
                .wholeSaleOrders( new ArrayList<>() )
                .retailProducts( new ArrayList<>() )
                .wholeSaleProducts( new ArrayList<>() )
                .build();
    }
}
