package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserCredential;
import com.elleined.marketplaceapi.model.user.UserDetails;
import com.elleined.marketplaceapi.model.user.UserVerification;
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

    public abstract UserCredential toUserCredentialEntity(UserDTO.UserCredentialDTO userCredentialDTO);

    @Mappings({
            @Mapping(target = "registrationDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "birthDate", source = "userDetailsDTO.birthDate")
    })
    public abstract UserDetails toUserDetailsEntity(UserDTO.UserDetailsDTO userDetailsDTO);
//
//    @Mappings({
//          // IGNORE PASSWORD here
//    })
//    public abstract UserDTO toDTO(User user);
//
//
//    @Mappings({
////          // IGNORE PASSWORD here
//    })
//    public abstract void toUpdate(UserDTO userDTO, @MappingTarget User user);
}
