package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.model.Moderator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {CredentialMapper.class})
public interface ModeratorMapper {

    @Mapping(target = "moderatorCredentialDTO", source = "moderatorCredential")
    ModeratorDTO toDTO(Moderator moderator);


    @Mappings({
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "listedProducts", expression = "java(new java.util.HashSet<>())"),
            @Mapping(target = "verifiedUsers", expression = "java(new java.util.HashSet<>())"),
            @Mapping(target = "moderatorCredential", source = "moderatorCredentialDTO")
    })
    Moderator toEntity(ModeratorDTO moderatorDTO);
}
