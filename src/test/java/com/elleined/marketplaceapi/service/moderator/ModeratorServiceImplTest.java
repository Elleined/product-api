package com.elleined.marketplaceapi.service.moderator;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionException;
import com.elleined.marketplaceapi.exception.atm.transaction.TransactionReleaseException;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.user.*;
import com.elleined.marketplaceapi.mapper.ModeratorMapper;
import com.elleined.marketplaceapi.mock.MultiPartFileDataFactory;
import com.elleined.marketplaceapi.model.Credential;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.atm.WithdrawTransactionRepository;
import com.elleined.marketplaceapi.service.image.ImageUploader;
import com.elleined.marketplaceapi.service.moderator.request.UserVerificationRequest;
import com.elleined.marketplaceapi.service.moderator.request.product.RetailProductRequest;
import com.elleined.marketplaceapi.service.moderator.request.product.WholeSaleProductRequest;
import com.elleined.marketplaceapi.service.moderator.request.transaction.DepositRequest;
import com.elleined.marketplaceapi.service.moderator.request.transaction.WithdrawRequest;
import com.elleined.marketplaceapi.service.password.ModeratorPasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
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
    private RetailProductRequest retailProductRequest;
    @Mock
    private WholeSaleProductRequest wholeSaleProductRequest;
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
    @DisplayName("login validation 1: email not exists")
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
    @DisplayName("login validation 2: incorrect password")
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
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.getAllUnverifiedUser());

        // Behavior verification
        verify(userVerificationRequest).getAllRequest();
    }

    @Test
    void verifyUser() {
        // Mock data
        User user = spy(User.class);

        // Stubbing methods
        doReturn(false).when(user).isVerified();
        doReturn(true).when(user).hasShopRegistration();
        doReturn(false).when(user).isRejected();

        doNothing().when(userVerificationRequest).accept(any(Moderator.class), any(User.class));

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.verifyUser(new Moderator(), user));

        // Behavior verification
        verify(userVerificationRequest).accept(any(Moderator.class), any(User.class));
    }

    @Test
    @DisplayName("verify user validations 1: verified user cannot be verified again")
    void verifiedUserCannotBeVerifiedAgainToReduceRedundancy() {
        // Mock data
        User user = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .build();

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(UserAlreadyVerifiedException.class, () -> moderatorService.verifyUser(new Moderator(), user));

        // Behavior verification
        verifyNoInteractions(userVerificationRequest);
    }

    @Test
    @DisplayName("verify user validation 2: users without shop registration cannot be verified and should submit shop registration first!")
    void userWithoutShopRegistrationCannotBeVerified() {
        // Mock data
        User user = spy(User.class);
        user.setShop(null);

        // Stubbing methods
        doReturn(false).when(user).isVerified();

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(NoShopRegistrationException.class, () -> moderatorService.verifyUser(new Moderator(), user));

        // Behavior verification
        verifyNoInteractions(userVerificationRequest);
    }

    @Test
    @DisplayName("verify user validation 2: users without shop registration and rejected cannot be verified and should resend valid id!")
    void userWithoutShopRegistrationAndThatIsRejectedCannotBeVerified() {
        // Mock data
        User user = spy(User.class);
        user.setUserVerification(UserVerification.builder()
                .validId(null) // Meaning that is rejected if valid id is null
                .build());

        // Stubbing methods
        doReturn(false).when(user).isVerified();
        doReturn(true).when(user).hasShopRegistration();
        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(UserVerificationRejectionException.class, () -> moderatorService.verifyUser(new Moderator(), user));

        // Behavior verification
        verifyNoInteractions(userVerificationRequest);
    }

    @Test
    void verifyAllUser() {
        // Mock Data
        Moderator moderator = new Moderator();
        Set<User> unVerifiedUsers = new HashSet<>();
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.verifyAllUser(moderator, unVerifiedUsers));

        // Behavior verification
        verify(userVerificationRequest).acceptAll(moderator, unVerifiedUsers);
    }

    @Test
    void rejectUser() {
        // Mock data
        User user = User.builder()
                .userVerification(new UserVerification())
                .build();

        // Stubbing methods
        doNothing().when(userVerificationRequest).reject(any(Moderator.class), any(User.class));

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.rejectUser(new Moderator(), user));

        // Behavior verification
        verify(userVerificationRequest).reject(any(Moderator.class), any(User.class));
    }

    @Test
    @DisplayName("reject user validations 1: verified user cannot be rejected again")
    void verifiedUserCannotBeRejectedAgain() {
        // Mock data
        User user = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .build();

        // Stubbing methods

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertThrowsExactly(UserAlreadyVerifiedException.class, () -> moderatorService.rejectUser(new Moderator(), user));

        // Behavior verification
        verifyNoInteractions(userVerificationRequest);
    }

    @Test
    void rejectAllUser() {
        // Mock data
        Moderator moderator = new Moderator();
        Set<User> unVerifiedUsers = new HashSet<>();

        // Stubbing methods
        doNothing().when(userVerificationRequest).rejectAll(moderator, unVerifiedUsers);

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.rejectAllUser(moderator, unVerifiedUsers));

        // Behavior verification
        verify(userVerificationRequest).rejectAll(moderator, unVerifiedUsers);
    }

    @Test
    void getAllPendingRetailProduct() {
        // Mock data
        // Stubbing methods
        when(retailProductRequest.getAllRequest()).thenReturn(new ArrayList<>());

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.getAllPendingRetailProduct());

        // Behavior verification
        verify(retailProductRequest).getAllRequest();
    }

    @Test
    void listRetailProduct() {
        // Mock data
        Moderator moderator = new Moderator();
        RetailProduct retailProduct = new RetailProduct();
        // Stubbing methods
        doNothing().when(retailProductRequest).accept(moderator, retailProduct);

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.listRetailProduct(moderator, retailProduct));

        // Behavior verification
        verify(retailProductRequest).accept(moderator, retailProduct);
    }

    @Test
    void listAllRetailProduct() {
        // Mock data
        Moderator moderator = new Moderator();
        Set<RetailProduct> retailProducts = new HashSet<>();

        // Stubbing methods
        doNothing().when(retailProductRequest).acceptAll(moderator, retailProducts);

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.listAllRetailProduct(moderator, retailProducts));

        // Behavior verification
        verify(retailProductRequest).acceptAll(moderator, retailProducts);
    }

    @Test
    void rejectRetailProduct() {
        // Mock data
        Moderator moderator = new Moderator();
        RetailProduct retailProduct = new RetailProduct();

        // Stubbing methods
        doNothing().when(retailProductRequest).reject(moderator, retailProduct);

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.rejectRetailProduct(moderator, retailProduct));

        // Behavior verification
        verify(retailProductRequest).reject(moderator, retailProduct);
    }

    @Test
    void rejectAllRetailProduct() {
        // Mock data
        Moderator moderator = new Moderator();
        Set<RetailProduct> retailProducts = new HashSet<>();

        // Stubbing methods
        doNothing().when(retailProductRequest).rejectAll(moderator, retailProducts);

        // Expected/ Actual values
        // Calling the method
        // Assertions
        moderatorService.rejectAllRetailProduct(moderator, retailProducts);

        // Behavior verification
        verify(retailProductRequest).rejectAll(moderator, retailProducts);
    }

    @Test
    void getAllPendingWholeSaleProduct() {
        // Mock data
        // Stubbing methods
        when(wholeSaleProductRequest.getAllRequest()).thenReturn(new ArrayList<>());

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.getAllPendingWholeSaleProduct());

        // Behavior verification
        verify(wholeSaleProductRequest).getAllRequest();
    }

    @Test
    void listWholeSaleProduct() {
        // Mock data
        Moderator moderator = new Moderator();
        WholeSaleProduct wholeSaleProduct = new WholeSaleProduct();
        // Stubbing methods
        doNothing().when(wholeSaleProductRequest).accept(moderator, wholeSaleProduct);

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.listWholeSaleProduct(moderator, wholeSaleProduct));

        // Behavior verification
        verify(wholeSaleProductRequest).accept(moderator, wholeSaleProduct);
    }

    @Test
    void listAllWholeSaleProduct() {
        // Mock data
        Moderator moderator = new Moderator();
        Set<WholeSaleProduct> wholeSaleProducts = new HashSet<>();

        // Stubbing methods
        doNothing().when(wholeSaleProductRequest).acceptAll(any(Moderator.class), anySet());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.listAllWholeSaleProduct(moderator, wholeSaleProducts));

        // Behavior verification
        verify(wholeSaleProductRequest).acceptAll(any(Moderator.class), anySet());
    }

    @Test
    void rejectWholeSaleProduct() {
// Mock data
        Moderator moderator = new Moderator();
        WholeSaleProduct wholeSaleProduct = new WholeSaleProduct();

        // Stubbing methods
        doNothing().when(wholeSaleProductRequest).reject(moderator, wholeSaleProduct);

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.rejectWholeSaleProduct(moderator, wholeSaleProduct));

        // Behavior verification
        verify(wholeSaleProductRequest).reject(moderator, wholeSaleProduct);
    }

    @Test
    void rejectAllWholeSaleProduct() {
        // Mock data
        Moderator moderator = new Moderator();
        Set<WholeSaleProduct> wholeSaleProducts = new HashSet<>();

        // Stubbing methods
        doNothing().when(wholeSaleProductRequest).rejectAll(moderator, wholeSaleProducts);

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.rejectAllWholeSaleProduct(moderator, wholeSaleProducts));

        // Behavior verification
        verify(wholeSaleProductRequest).rejectAll(moderator, wholeSaleProducts);
    }

    @Test
    void getAllPendingDepositRequest() {
        // Mock data

        // Stubbing methods
        when(depositRequest.getAllRequest()).thenReturn(new ArrayList<>());

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.getAllPendingDepositRequest());

        // Behavior verification
        verify(depositRequest).getAllRequest();
    }

    @Test
    void release() {
        // Mock data

        // Stubbing methods
        doNothing().when(depositRequest).accept(any(Moderator.class), any(DepositTransaction.class));

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.release(new Moderator(), new DepositTransaction()));

        // Behavior verification
        verify(depositRequest).accept(any(Moderator.class), any(DepositTransaction.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"REJECTED", "RELEASE"})
    @DisplayName("reject deposit transaction validation 1: rejected and release transactions cannot be released again")
    void depositTransactionsThatIsAlreadyBeenRejectedOrReleaseCannotBeReleaseAgain(String transactionStatus) {
        // Mock data
        DepositTransaction depositTransaction = DepositTransaction.builder()
                .status(Transaction.Status.valueOf(transactionStatus))
                .build();

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrows(TransactionException.class, () -> moderatorService.release(new Moderator(), depositTransaction));

        // Behavior verification
        verifyNoInteractions(depositRequest);
    }

    @Test
    void releaseAllDepositRequest() {
        // Mock data

        // Stubbing methods
        doNothing().when(depositRequest).acceptAll(any(Moderator.class), anySet());

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.releaseAllDepositRequest(new Moderator(), new HashSet<>()));

        // Behavior verification
        verify(depositRequest).acceptAll(any(Moderator.class), anySet());
    }

    @Test
    void reject() {
        // Mock data
        // Stubbing methods
        doNothing().when(depositRequest).reject(any(Moderator.class), any(DepositTransaction.class));

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.reject(new Moderator(), new DepositTransaction()));

        // Behavior verification
        verify(depositRequest).reject(any(Moderator.class), any(DepositTransaction.class));
    }

    @Test
    @DisplayName("reject deposit request validation 1: released transaction cannot be rejected ")
    void depositTransactionThatAlreadyBeenReleaseCannotBeRejected() {
        // Mock data
        DepositTransaction depositTransaction = DepositTransaction.builder()
                .status(Transaction.Status.RELEASE)
                .build();

        // Stubbing methods

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertThrowsExactly(TransactionReleaseException.class, () -> moderatorService.reject(new Moderator(), depositTransaction));

        // Behavior verification
        verifyNoInteractions(depositRequest);
    }

    @Test
    void rejectAllDepositRequest() {
        // Mock data

        // Stubbing methods
        doNothing().when(depositRequest).rejectAll(any(Moderator.class), anySet());

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.rejectAllDepositRequest(new Moderator(), new HashSet<>()));

        // Behavior verification
        verify(depositRequest).rejectAll(any(Moderator.class), anySet());
    }

    @Test
    void getAllPendingWithdrawRequest() {
        // Mock data

        // Stubbing methods
        when(withdrawRequest.getAllRequest()).thenReturn(new ArrayList<>());

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.getAllPendingWithdrawRequest());

        // Behavior verification
        verify(withdrawRequest).getAllRequest();
    }

    @Test
    void releaseWithdraw() {
        // Mock data
        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .user(User.builder()
                        .balance(new BigDecimal(5000))
                        .build())
                .amount(new BigDecimal(100))
                .status(Transaction.Status.PENDING)
                .build();

        // Stubbing methods
        doNothing().when(withdrawRequest).accept(any(Moderator.class), any(WithdrawTransaction.class));

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.release(new Moderator(), withdrawTransaction , MultiPartFileDataFactory.notEmpty()));

        // Behavior verification
        verify(withdrawRequest).accept(any(Moderator.class), any(WithdrawTransaction.class));
    }

    @Test
    @DisplayName("release withdraw request validation 1: cannot release withdraw transaction with invalid proof of transaction")
    void withdrawTransactionCannotBeReleaseIfProofOfTransactionIsNotValid() {
        // Mock data
        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .user(new User())
                .amount(new BigDecimal(100))
                .build();
        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(NotValidBodyException.class, () -> moderatorService.release(new Moderator(), withdrawTransaction, MultiPartFileDataFactory.empty()));

        // Behavior verification
        verifyNoInteractions(withdrawTransactionRepository, imageUploader, withdrawRequest);
    }

    @ParameterizedTest
    @ValueSource(strings = {"RELEASE", "REJECTED"})
    @DisplayName("release withdraw request validation 2: release and rejected withdraw transaction cannot be released")
    void name(String transactionStatus) {
        // Mock data
        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .user(new User())
                .status(Transaction.Status.valueOf(transactionStatus))
                .amount(new BigDecimal(100))
                .build();

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrows(TransactionException.class, () -> moderatorService.release(new Moderator(), withdrawTransaction, MultiPartFileDataFactory.notEmpty()));

        // Behavior verification
        verifyNoInteractions(withdrawTransactionRepository, imageUploader, withdrawRequest);
    }

    @Test
    @DisplayName("release withdraw request validation 2: cannot proceed if balance is not enough")
    void cannotReleaseWithdrawTransactionIfTheRequestingUserBalanceAreNotEnough() {
        // Mock data
        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .user(User.builder()
                        .balance(new BigDecimal(5))
                        .build())
                .status(Transaction.Status.PENDING)
                .amount(new BigDecimal(10))
                .build();

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(InsufficientBalanceException.class, () -> moderatorService.release(new Moderator(), withdrawTransaction, MultiPartFileDataFactory.notEmpty()));

        // Behavior verification
        verifyNoInteractions(withdrawTransactionRepository, imageUploader, withdrawRequest);
    }

    @Test
    void releaseAllWithdrawRequest() {
        // Mock data

        // Stubbing methods
        doNothing().when(withdrawRequest).acceptAll(any(Moderator.class), anySet());

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.releaseAllWithdrawRequest(new Moderator(), new HashSet<>()));

        // Behavior verification
        verify(withdrawRequest).acceptAll(any(Moderator.class), anySet());
    }

    @Test
    void rejectWithdraw() {
        // Mock data
        // Stubbing methods
        doNothing().when(withdrawRequest).reject(any(Moderator.class), any(WithdrawTransaction.class));

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.reject(new Moderator(), new WithdrawTransaction()));

        // Behavior verification
        verify(withdrawRequest).reject(any(Moderator.class), any(WithdrawTransaction.class));
    }

    @Test
    @DisplayName("reject withdraw request validation 1: released withdraw transaction cannot be rejected")
    void withdrawTransactionThatAlreadyBeenReleaseCannotBeRejected() {
        // Mock data
        WithdrawTransaction withdrawTransaction = WithdrawTransaction.builder()
                .status(Transaction.Status.RELEASE)
                .build();

        // Stubbing methods

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertThrowsExactly(TransactionReleaseException.class, () -> moderatorService.reject(new Moderator(), withdrawTransaction));

        // Behavior verification
        verifyNoInteractions(withdrawRequest);
    }

    @Test
    void rejectAllWithdrawRequest() {
        // Mock data

        // Stubbing methods
        doNothing().when(withdrawRequest).rejectAll(any(Moderator.class), anySet());

        // Expected/ Actual values
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> moderatorService.rejectAllWithdrawRequest(new Moderator(), new HashSet<>()));

        // Behavior verification
        verify(withdrawRequest).rejectAll(any(Moderator.class), anySet());
    }
}