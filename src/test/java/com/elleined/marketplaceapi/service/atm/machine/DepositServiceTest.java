package com.elleined.marketplaceapi.service.atm.machine;

import com.elleined.marketplaceapi.exception.atm.amount.DepositAmountBelowMinimumException;
import com.elleined.marketplaceapi.exception.atm.NotValidAmountException;
import com.elleined.marketplaceapi.exception.resource.PictureNotValidException;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.atm.DepositTransactionRepository;
import com.elleined.marketplaceapi.service.AppWalletService;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import com.elleined.marketplaceapi.service.image.ImageUploader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class DepositServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepositTransactionRepository depositTransactionRepository;

    @Mock
    private ATMFeeService feeService;

    @Mock
    private AppWalletService appWalletService;

    @Mock
    private ImageUploader imageUploader;

    @InjectMocks
    private DepositService depositService;

    @Test
    void deposit() {
        User user = User.builder()
                .balance(new BigDecimal(500))
                .build();

        BigDecimal amountToBeDeposited = new BigDecimal(400);
        float depositFee = 50;
        when(feeService.getDepositFee(amountToBeDeposited)).thenReturn(depositFee);
        depositService.deposit(user, amountToBeDeposited);

        verify(userRepository).save(user);
        verify(appWalletService).addAndSaveBalance(depositFee);
        assertEquals(new BigDecimal(850), user.getBalance());
    }

    @Test
    void requestDeposit() {
    }

    @Test
    void shouldThrowPictureNotValidException() {
        User user = new User();
        BigDecimal amount = new BigDecimal(500);

        MultipartFile nullMultiPartFile = null;
        MultipartFile emptyMultiPartFile = new MockMultipartFile("mockMultiPartFile", new byte[0]);

        assertThrows(PictureNotValidException.class, () -> depositService.requestDeposit(user, amount, nullMultiPartFile));
        assertThrows(PictureNotValidException.class, () -> depositService.requestDeposit(user, amount, emptyMultiPartFile));

        verifyNoInteractions(depositTransactionRepository);
        verifyNoInteractions(imageUploader);
    }

    @Test
    void shouldThrowBelowMinimumException() {
        User mockUser = new User();
        MultipartFile mockMultiPartFile = new MockMultipartFile("mockMultiPartFile", new byte[1]);
        BigDecimal belowMinimumAmount = new BigDecimal(499);

        assertThrows(DepositAmountBelowMinimumException.class, () -> depositService.requestDeposit(mockUser, belowMinimumAmount, mockMultiPartFile), "Failed because the deposit amount " + belowMinimumAmount + " is above to " + DepositService.MINIMUM_DEPOSIT_AMOUNT);
        verifyNoInteractions(depositTransactionRepository);
        verifyNoInteractions(imageUploader);
    }

    @Test
    void shouldThrowAboveMaximumException() {

    }

    @Test
    void shouldThrowNotValidAmountException() {
        User user = new User();
        MultipartFile mockMultiPartFile = new MockMultipartFile("mockMultiPartFile", new byte[1]);

        BigDecimal negativeAmount = new BigDecimal(-1);
        BigDecimal nullAmount = null;

        assertThrows(NotValidAmountException.class, () -> depositService.requestDeposit(user, nullAmount, mockMultiPartFile));
        assertThrows(NotValidAmountException.class, () -> depositService.requestDeposit(user, negativeAmount, mockMultiPartFile));
        verifyNoInteractions(depositTransactionRepository);
        verifyNoInteractions(imageUploader);
    }
}