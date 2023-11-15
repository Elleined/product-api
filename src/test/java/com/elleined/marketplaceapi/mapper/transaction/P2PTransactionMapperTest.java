package com.elleined.marketplaceapi.mapper.transaction;

import com.elleined.marketplaceapi.dto.atm.dto.PeerToPeerTransactionDTO;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.elleined.marketplaceapi.model.atm.transaction.Transaction.Status.PENDING;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class P2PTransactionMapperTest {

    @InjectMocks
    private P2PTransactionMapper p2PTransactionMapper = Mappers.getMapper(P2PTransactionMapper.class);

    @Test
    void toDTO() {
        PeerToPeerTransaction expected = PeerToPeerTransaction.builder()
                .id(1)
                .trn("TRN")
                .amount(new BigDecimal(600))
                .transactionDate(LocalDateTime.now())
                .status(PENDING)
                .proofOfTransaction("Proof of transction.jpg")
                .sender(User.builder()
                        .id(1)
                        .build())
                .receiver(User.builder()
                        .id(2)
                        .build())
                .build();

        float fee = 600F;
        PeerToPeerTransactionDTO actual = p2PTransactionMapper.toDTO(expected, fee);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTrn(), actual.getTrn());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getTransactionDate(), actual.getTransactionDate());
        assertEquals(expected.getProofOfTransaction(), actual.getProofOfTransaction());
        assertEquals(fee, actual.getTransactionFee());
        assertEquals(expected.getSender().getId(), actual.getSenderId());
        assertEquals(expected.getReceiver().getId(), actual.getReceiverId());
    }
}