package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, CredentialMapper.class})
public interface UserMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "shop", ignore = true),
            @Mapping(target = "userVerification.validId", ignore = true),
            @Mapping(target = "address", ignore = true), // will be saved after saving user
            @Mapping(target = "premium", ignore = true),

            @Mapping(target = "referralCode", expression = "java(java.util.UUID.randomUUID().toString())"),

            @Mapping(target = "cartItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "orderedItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "deliveryAddresses", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "products", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "referredUsers", expression = "java(new java.util.HashSet<>())"),
            @Mapping(target = "balance", expression = "java(new java.math.BigDecimal(0))"),

            @Mapping(target = "userVerification.status", expression = "java(UserVerification.Status.NOT_VERIFIED)"),
            @Mapping(target = "userDetails", expression = "java(toUserDetailsEntity(userDTO.getUserDetailsDTO()))"),
            @Mapping(target = "userCredential", source = "userCredentialDTO"),

            @Mapping(target = "depositTransactions", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "receiveMoneyTransactions", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "sentMoneyTransactions", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "withdrawTransactions", expression = "java(new java.util.ArrayList<>())")
    })
    User toEntity(UserDTO userDTO);

    @Mappings({
            @Mapping(target = "invitationReferralCode", ignore = true),
            @Mapping(target = "validId", source = "userVerification.validId"),
            @Mapping(target = "status", source = "userVerification.status"),

            @Mapping(target = "addressDTO", source = "address"),
            @Mapping(target = "userCredentialDTO", source = "userCredential"),
            @Mapping(target = "userDetailsDTO", expression = "java(toUserDetailsDTO(user.getUserDetails()))"),
            @Mapping(target = "userDetailsDTO.suffix", source = "userDetails.suffix"),
            @Mapping(target = "isPremium", expression = "java(user.isPremium())")
    })
    UserDTO toDTO(User user);

    @Mappings({
            @Mapping(target = "registrationDate", expression = "java(java.time.LocalDateTime.now())"),
    })
    UserDetails toUserDetailsEntity(UserDTO.UserDetailsDTO userDetailsDTO);

    @Mappings({
            @Mapping(target = "gender", source = "userDetails.gender"),
            @Mapping(target = "picture", source = "picture"),
            @Mapping(target = "suffix", source = "userDetails.suffix"),
    })
    UserDTO.UserDetailsDTO toUserDetailsDTO(UserDetails userDetails);
}
