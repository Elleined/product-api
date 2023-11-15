package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.model.Credential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CredentialMapperTest {

    @InjectMocks
    private CredentialMapper credentialMapper = Mappers.getMapper(CredentialMapper.class);

    @Test
    void toDTO() {
        Credential credential = Credential.builder()
                .email("email")
                .password("Password")
                .build();

        CredentialDTO credentialDTO = credentialMapper.toDTO(credential);

        assertNotNull(credentialDTO.getEmail());

        assertNull(credentialDTO.getConfirmPassword());
        assertNull(credentialDTO.getPassword());
    }

    @Test
    void toEntity() {
        CredentialDTO expected = CredentialDTO.builder()
                .email("Email")
                .password("Password")
                .build();

        Credential actual = credentialMapper.toEntity(expected);

        assertEquals(expected.getEmail(), actual.getEmail());
        assertNotNull(actual.getEmail());

        assertEquals(expected.getPassword(), actual.getPassword());
        assertNotNull(actual.getPassword());
    }
}