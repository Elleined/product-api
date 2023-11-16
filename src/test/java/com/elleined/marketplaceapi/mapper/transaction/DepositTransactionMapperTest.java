package com.elleined.marketplaceapi.mapper.transaction;

import com.elleined.marketplaceapi.dto.atm.dto.DepositTransactionDTO;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
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
class DepositTransactionMapperTest {

    @InjectMocks
    private DepositTransactionMapper depositTransactionMapper = Mappers.getMapper(DepositTransactionMapper.class);

    @Test
    void toDTO() {
        DepositTransaction expected = DepositTransaction.builder()
                .id(1)
                .trn("TRN")
                .amount(new BigDecimal(600))
                .transactionDate(LocalDateTime.now())
                .status(PENDING)
                .proofOfTransaction("Proof of transction.jpg")
                .user(User.builder()
                        .id(1)
                        .build())
                .build();

        float fee = 500F;
        DepositTransactionDTO actual = depositTransactionMapper.toDTO(expected, fee);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTrn(), actual.getTrn());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getTransactionDate(), actual.getTransactionDate());
        assertEquals(expected.getProofOfTransaction(), actual.getProofOfTransaction());
        assertEquals(fee, actual.getTransactionFee());
        assertEquals(expected.getUser().getId(), actual.getUserId());
    }
}