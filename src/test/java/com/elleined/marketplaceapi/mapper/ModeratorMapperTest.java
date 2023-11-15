package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.model.Credential;
import com.elleined.marketplaceapi.model.Moderator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModeratorMapperTest {

    @Spy
    private CredentialMapper credentialMapper = Mappers.getMapper(CredentialMapper.class);

    @InjectMocks
    private ModeratorMapper moderatorMapper = Mappers.getMapper(ModeratorMapper.class);


    @Test
    void toDTO() {
        Credential credential = Credential.builder()
                .email("email")
                .password("Password")
                .build();

        Moderator expected = Moderator.builder()
                .id(1)
                .name("Name")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .moderatorCredential(credential)
                .listedProducts(new HashSet<>())
                .rejectedProducts(new HashSet<>())
                .verifiedUsers(new HashSet<>())
                .releaseDepositRequest(new HashSet<>())
                .rejectedDepositRequest(new HashSet<>())
                .releaseWithdrawRequests(new HashSet<>())
                .rejectedWithdrawRequests(new HashSet<>())
                .build();

        ModeratorDTO actual = moderatorMapper.toDTO(expected);

        verify(credentialMapper).toDTO(credential);

        assertEquals(expected.getId(), actual.id());
        assertEquals(expected.getName(), actual.name());

        assertNotNull(actual.moderatorCredentialDTO());
        assertNotNull(actual.moderatorCredentialDTO().getEmail());

        assertNull(actual.moderatorCredentialDTO().getConfirmPassword());
        assertNull(actual.moderatorCredentialDTO().getPassword());
    }

    @Test
    void toEntity() {
        CredentialDTO credentialDTO = CredentialDTO.builder()
                .email("Email")
                .password("Password")
                .build();

        ModeratorDTO moderatorDTO = ModeratorDTO.builder()
                .name("Name")
                .moderatorCredentialDTO(credentialDTO)
                .build();

        Moderator moderator = moderatorMapper.toEntity(moderatorDTO);
        verify(credentialMapper).toEntity(credentialDTO);

        
        assertEquals(0, moderator.getId());
        assertNotNull(moderator.getName());

        assertNotNull(moderator.getListedProducts());
        assertTrue(moderator.getListedProducts().isEmpty());

        assertNotNull(moderator.getRejectedProducts());
        assertTrue(moderator.getRejectedProducts().isEmpty());

        assertNotNull(moderator.getVerifiedUsers());
        assertTrue(moderator.getVerifiedUsers().isEmpty());

        assertNotNull(moderator.getReleaseDepositRequest());
        assertTrue(moderator.getReleaseDepositRequest().isEmpty());

        assertNotNull(moderator.getRejectedDepositRequest());
        assertTrue(moderator.getRejectedDepositRequest().isEmpty());

        assertNotNull(moderator.getReleaseWithdrawRequests());
        assertTrue(moderator.getReleaseWithdrawRequests().isEmpty());

        assertNotNull(moderator.getRejectedWithdrawRequests());
        assertTrue(moderator.getRejectedWithdrawRequests().isEmpty());

        assertEquals(credentialDTO.getEmail(), moderatorDTO.moderatorCredentialDTO().getEmail());
        assertNotNull(moderatorDTO.moderatorCredentialDTO().getEmail());

        assertEquals(credentialDTO.getPassword(), moderatorDTO.moderatorCredentialDTO().getPassword());
        assertNotNull(moderatorDTO.moderatorCredentialDTO().getPassword());
    }
}