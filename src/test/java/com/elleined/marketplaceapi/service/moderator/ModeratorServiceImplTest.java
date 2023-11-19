package com.elleined.marketplaceapi.service.moderator;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.exception.user.InvalidUserCredentialException;
import com.elleined.marketplaceapi.mapper.ModeratorMapper;
import com.elleined.marketplaceapi.model.Credential;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.atm.WithdrawTransactionRepository;
import com.elleined.marketplaceapi.service.image.ImageUploader;
import com.elleined.marketplaceapi.service.moderator.request.DepositRequest;
import com.elleined.marketplaceapi.service.moderator.request.UserVerificationRequest;
import com.elleined.marketplaceapi.service.moderator.request.WithdrawRequest;
import com.elleined.marketplaceapi.service.moderator.request.product.ProductRequest;
import com.elleined.marketplaceapi.service.password.ModeratorPasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModeratorServiceImplTest {
    @Mock
    private ModeratorRepository moderatorRepository;
    @Mock
    private ModeratorMapper moderatorMapper;
    @Mock
    private ModeratorPasswordEncoder moderatorPasswordEncoder;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserVerificationRequest userVerificationRequest;
    @Mock
    private ProductRequest<RetailProduct> retailProductRequest;
    @Mock
    private ProductRequest<WholeSaleProduct> wholeSaleProductRequest;
    @Mock
    private WithdrawRequest withdrawRequest;
    @Mock
    private DepositRequest depositRequest;
    @Mock
    private WithdrawTransactionRepository withdrawTransactionRepository;
    @Mock
    private ImageUploader imageUploader;
    @InjectMocks
    private ModeratorServiceImpl moderatorService;

    @Test
    void save() {
        // Mock data
        ModeratorDTO moderatorDTO = ModeratorDTO.builder()
                .moderatorCredentialDTO(CredentialDTO.builder()
                        .password("password")
                        .build())
                .build();

        Moderator moderator = Moderator.builder()
                .moderatorCredential(new Credential())
                .build();

        String expectedPassword = "encoded password";

        // Stubbing methods
        when(moderatorMapper.toEntity(any(ModeratorDTO.class))).thenReturn(new Moderator());
        doAnswer(i -> {
            moderator.getModeratorCredential().setPassword(expectedPassword);
            return moderatorDTO;
        }).when(moderatorPasswordEncoder).encodePassword(any(Moderator.class), anyString());
        when(moderatorRepository.save(any(Moderator.class))).thenReturn(moderator);

        // Expected/ Actual values

        // Calling the method
        moderatorService.save(moderatorDTO);

        // Assertions
        assertEquals(expectedPassword, moderator.getModeratorCredential().getPassword());

        // Behavior verification
        verify(moderatorMapper).toEntity(any(ModeratorDTO.class));
        verify(moderatorPasswordEncoder).encodePassword(any(Moderator.class), anyString());
        verify(moderatorRepository).save(any(Moderator.class));
    }

    @Test
    void login() {
        // Mock data
        String email = "email";

        List<String> emails = List.of(email);

        ModeratorDTO moderatorDTO = ModeratorDTO.builder()
                .moderatorCredentialDTO(CredentialDTO.builder()
                        .email(email)
                        .password("password")
                        .build())
                .build();

        Moderator moderator = new Moderator();

        // Stubbing methods
        when(moderatorRepository.fetchAllEmail()).thenReturn(emails);
        when(moderatorRepository.fetchByEmail(anyString())).thenReturn(Optional.of(moderator));
        when(moderatorPasswordEncoder.matches(any(Moderator.class), anyString())).thenReturn(true);
        when(moderatorMapper.toDTO(any(Moderator.class))).thenReturn(moderatorDTO);
        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.login(moderatorDTO.moderatorCredentialDTO()));

        // Behavior verification
        verify(moderatorRepository).fetchAllEmail();
        verify(moderatorRepository).fetchByEmail(anyString());
        verify(moderatorPasswordEncoder).matches(any(Moderator.class), anyString());
        verify(moderatorMapper).toDTO(any(Moderator.class));
    }

    @Test
    @DisplayName("login validation 1")
    void shouldThrowInvalidCredentialExceptionIfEmailNotExists() {
        // Mock data
        List<String> emails = Collections.emptyList();

        String email = "email@gmail.com";
        ModeratorDTO moderatorDTO = ModeratorDTO.builder()
                .moderatorCredentialDTO(CredentialDTO.builder()
                        .email(email)
                        .password("password")
                        .build())
                .build();

        // Stubbing methods
        when(moderatorRepository.fetchAllEmail()).thenReturn(emails);

        // Calling the method
        // Assertions
        assertThrowsExactly(InvalidUserCredentialException.class, () -> moderatorService.login(moderatorDTO.moderatorCredentialDTO()));

        // Behavior verification
        verify(moderatorRepository).fetchAllEmail();
        verifyNoMoreInteractions(moderatorRepository);
        verifyNoInteractions(moderatorPasswordEncoder, moderatorMapper);
    }

    @Test
    @DisplayName("login validation 2")
    void shouldThrowInvalidCredentialExceptionIfPasswordIsIncorrect() {
        // Mock data
        String email = "email@gmail.com";
        List<String> emails = List.of(email);
        ModeratorDTO moderatorDTO = ModeratorDTO.builder()
                .moderatorCredentialDTO(CredentialDTO.builder()
                        .email(email)
                        .password("password")
                        .build())
                .build();

        // Stubbing methods
        when(moderatorRepository.fetchAllEmail()).thenReturn(emails);
        when(moderatorRepository.fetchByEmail(email)).thenReturn(Optional.of(new Moderator()));
        when(moderatorPasswordEncoder.matches(any(Moderator.class), anyString())).thenReturn(false);

        // Calling the method
        // Assertions
        assertThrowsExactly(InvalidUserCredentialException.class, () -> moderatorService.login(moderatorDTO.moderatorCredentialDTO()));

        // Behavior verification
        verify(moderatorRepository).fetchAllEmail();
        verify(moderatorRepository).fetchByEmail(anyString());
        verify(moderatorPasswordEncoder).matches(any(Moderator.class), anyString());
        verifyNoInteractions(moderatorMapper);
    }

    @Test
    void getAllUnverifiedUser() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void verifyUser() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void verifyAllUser() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void rejectUser() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void rejectAllUser() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void getAllPendingRetailProduct() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void listRetailProduct() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void listAllRetailProduct() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void rejectRetailProduct() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void rejectAllRetailProduct() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void getAllPendingWholeSaleProduct() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void listWholeSaleProduct() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void listAllWholeSaleProduct() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void rejectWholeSaleProduct() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void rejectAllWholeSaleProduct() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void getAllPendingDepositRequest() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void release() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void releaseAllDepositRequest() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void reject() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void rejectAllDepositRequest() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void getAllPendingWithdrawRequest() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void testRelease() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void releaseAllWithdrawRequest() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void testReject() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void rejectAllWithdrawRequest() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void encodePassword() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }
}