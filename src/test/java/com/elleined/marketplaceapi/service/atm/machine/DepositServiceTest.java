package com.elleined.marketplaceapi.service.atm.machine;

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
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
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
        MultipartFile proofOfTransaction = null;

        verifyNoInteractions(depositTransactionRepository);
        verifyNoInteractions(imageUploader);
        assertThrows(PictureNotValidException.class, () -> depositService.requestDeposit(user, amount, proofOfTransaction));
    }
}