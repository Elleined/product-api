package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserCredential;
import com.elleined.marketplaceapi.model.user.UserDetails;
import com.elleined.marketplaceapi.service.user.SuffixService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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

            @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID().toString())"),
            @Mapping(target = "suffix", expression = "java(suffixService.getByName(userDTO.getSuffix()))"),

            @Mapping(target = "cartItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "orderedItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "deliveryAddresses", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "products", expression = "java(new java.util.ArrayList<>())"),

            @Mapping(target = "userVerification.status", expression = "java(UserVerification.Status.NOT_VERIFIED)"),

            @Mapping(target = "userDetails", expression = "java(toUserDetailsEntity(userDTO.getUserDetailsDTO()))"),
            @Mapping(target = "userCredential", expression = "java(toUserCredentialEntity(userDTO.getUserCredentialDTO()))"),
    })
    public abstract User toEntity(UserDTO userDTO);

    @Mappings({
            @Mapping(target = "validId", source = "user.userVerification.validId"),
            @Mapping(target = "status", source = "user.userVerification.status"),
            @Mapping(target = "suffix", source = "user.suffix.name"),

            @Mapping(target = "addressDTO", source = "user.address"),
            @Mapping(target = "userCredentialDTO", expression = "java(toUserCredentialDTO(user.getUserCredential()))"),
            @Mapping(target = "userDetailsDTO", expression = "java(toUserDetailsDTO(user.getUserDetails()))"),
    })
    public abstract UserDTO toDTO(User user);


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "uuid", ignore = true),
            @Mapping(target = "cartItems", ignore = true),
            @Mapping(target = "deliveryAddresses", ignore = true),
            @Mapping(target = "orderedItems", ignore = true),
            @Mapping(target = "products", ignore = true),
            @Mapping(target = "userVerification", ignore = true),
            @Mapping(target = "shop", ignore = true),

            @Mapping(target = "userCredential", ignore = true),

            @Mapping(target = "suffix", expression = "java(suffixService.getByName(userDTO.getSuffix()))"),

            @Mapping(target = "userDetails", source = "userDTO.userDetailsDTO"),
            @Mapping(target = "userDetails.picture", source = "userDTO.userDetailsDTO.picture"),
            @Mapping(target = "userDetails.gender", ignore = true),
            @Mapping(target = "userDetails.registrationDate", ignore = true),
            @Mapping(target = "userDetails.birthDate", ignore = true),

            @Mapping(target = "address", source = "userDTO.addressDTO"),
            @Mapping(target = "address.id", ignore = true),
    })
    public abstract User toUpdate(UserDTO userDTO, @MappingTarget User user);

    protected abstract UserCredential toUserCredentialEntity(UserDTO.UserCredentialDTO userCredentialDTO);


    @Mappings({
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "confirmPassword", ignore = true)
    })
    protected abstract UserDTO.UserCredentialDTO toUserCredentialDTO(UserCredential userCredential);


    @Mappings({
            @Mapping(target = "registrationDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "picture", source = "picture")
    })
    protected abstract UserDetails toUserDetailsEntity(UserDTO.UserDetailsDTO userDetailsDTO);

    @Mappings({
            @Mapping(target = "gender", source = "userDetails.gender"),
            @Mapping(target = "picture", source = "picture")
    })
    protected abstract UserDTO.UserDetailsDTO toUserDetailsDTO(UserDetails userDetails);
}
