package com.elleined.marketplaceapi.dto;

import lombok.Builder;

@Builder
public record ModeratorDTO(
        int id,
        String name,
        CredentialDTO moderatorCredentialDTO) {

}
