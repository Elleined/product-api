package com.elleined.marketplaceapi.mapper.transaction;

import com.elleined.marketplaceapi.dto.atm.dto.WithdrawTransactionDTO;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.elleined.marketplaceapi.model.atm.transaction.Transaction.Status.PENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class WithdrawTransactionMapperTest {

    @InjectMocks
    private WithdrawTransactionMapper withdrawTransactionMapper = Mappers.getMapper(WithdrawTransactionMapper.class);
    @Test
    void toDTO() {
        WithdrawTransaction expected = WithdrawTransaction.builder()
                .id(1)
                .trn("TRN")
                .amount(new BigDecimal(600))
                .transactionDate(LocalDateTime.now())
                .status(PENDING)
                .proofOfTransaction("Proof of transction.jpg")
                .user(User.builder()
                        .id(1)
                        .build())
                .gcashNumber("09999999999")
                .build();

        float fee = 900F;
        WithdrawTransactionDTO actual = withdrawTransactionMapper.toDTO(expected, fee);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTrn(), actual.getTrn());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getTransactionDate(), actual.getTransactionDate());
        assertEquals(expected.getProofOfTransaction(), actual.getProofOfTransaction());
        assertEquals(fee, actual.getTransactionFee());
        assertEquals(expected.getUser().getId(), actual.getUserId());
        assertEquals(expected.getGcashNumber(), actual.getGcashNumber());
    }
}