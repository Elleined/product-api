package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.model.Credential;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CredentialMapper {

    @Mappings({
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "confirmPassword", ignore = true)
    })
    CredentialDTO toDTO(Credential credential);


    Credential toEntity(CredentialDTO credentialDTO);
}
