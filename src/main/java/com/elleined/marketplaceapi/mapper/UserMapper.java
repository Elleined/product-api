package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.UserCredentialDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserCredential;
import com.elleined.marketplaceapi.service.user.SuffixService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired @Lazy
    protected SuffixService suffixService;


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "uuid", ignore = true),
            @Mapping(target = "shop", ignore = true),
            @Mapping(target = "userVerification.validId", ignore = true),

            @Mapping(target = "cartItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "orderedItems", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "deliveryAddresses", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "products", expression = "java(new java.util.ArrayList<>())"),

            @Mapping(target = "suffix", expression = "java(suffixService.getByName(userDTO.getSuffix()))"),
            @Mapping(target = "userDetails.firstName", source = "userDTO.firstName"),
            @Mapping(target = "userDetails.middleName", source = "userDTO.middleName"),
            @Mapping(target = "userDetails.lastName", source = "userDTO.lastName"),
            @Mapping(target = "userDetails.registrationDate", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "userDetails.gender", source = "userDTO.gender"),
            @Mapping(target = "userDetails.birthDate", source = "userDTO.birthDate"),
            @Mapping(target = "userDetails.mobileNumber", source = "userDTO.mobileNumber"),
            @Mapping(target = "userVerification.status", expression = "java(UserVerification.Status.NOT_VERIFIED)"),

            @Mapping(target = "address", ignore = true), // This will be saved after saving of user
            @Mapping(target = "userCredential", ignore = true) // This will be saved after saving of user
    })
    public abstract User toEntity(UserDTO userDTO);


    public abstract UserCredential toUserCredentialEntity(UserCredentialDTO userCredentialDTO);
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
