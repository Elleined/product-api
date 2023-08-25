package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserCredential;
import com.elleined.marketplaceapi.model.user.UserDetails;
import com.elleined.marketplaceapi.service.user.SuffixService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public abstract class UserMapper {

    @Autowired @Lazy
    protected SuffixService suffixService;


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "shop", ignore = true),
            @Mapping(target = "userVerification.validId", ignore = true),
            @Mapping(target = "address", ignore = true), // will be saved after saving user

            @Mapping(target = "referralCode", expression = "java(java.util.UUID.randomUUID().toString())"),

            @Mapping(target = "cartItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "orderedItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "deliveryAddresses", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "products", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "referredUsers", expression = "java(new java.util.HashSet<>())"),
            @Mapping(target = "balance", expression = "java(new java.math.BigDecimal(0))"),

            @Mapping(target = "userVerification.status", expression = "java(UserVerification.Status.NOT_VERIFIED)"),
            @Mapping(target = "userDetails", expression = "java(toUserDetailsEntity(userDTO.getUserDetailsDTO()))"),
            @Mapping(target = "userCredential", expression = "java(toUserCredentialEntity(userDTO.getUserCredentialDTO()))"),
            @Mapping(target = "isPremium", expression = "java(false)")
    })
    public abstract User toEntity(UserDTO userDTO);

    @Mappings({
            @Mapping(target = "invitationReferralCode", ignore = true),
            @Mapping(target = "validId", source = "userVerification.validId"),
            @Mapping(target = "status", source = "userVerification.status"),

            @Mapping(target = "addressDTO", source = "address"),
            @Mapping(target = "userCredentialDTO", expression = "java(toUserCredentialDTO(user.getUserCredential()))"),
            @Mapping(target = "userDetailsDTO", expression = "java(toUserDetailsDTO(user.getUserDetails()))"),
            @Mapping(target = "userDetailsDTO.suffix", source = "userDetails.suffix.name"),
    })
    public abstract UserDTO toDTO(User user);


    protected abstract UserCredential toUserCredentialEntity(UserDTO.UserCredentialDTO userCredentialDTO);


    @Mappings({
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "confirmPassword", ignore = true)
    })
    protected abstract UserDTO.UserCredentialDTO toUserCredentialDTO(UserCredential userCredential);


    @Mappings({
            @Mapping(target = "registrationDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "suffix", expression = "java(suffixService.getByName(userDetailsDTO.getSuffix()))"),
    })
    protected abstract UserDetails toUserDetailsEntity(UserDTO.UserDetailsDTO userDetailsDTO);

    @Mappings({
            @Mapping(target = "gender", source = "userDetails.gender"),
            @Mapping(target = "picture", source = "picture"),
            @Mapping(target = "suffix", source = "userDetails.suffix.name"),
    })
    protected abstract UserDTO.UserDetailsDTO toUserDetailsDTO(UserDetails userDetails);
}
