package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.atm.dto.DepositTransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.PeerToPeerTransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.TransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.WithdrawTransactionDTO;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "userId", source = "user.id")
    DepositTransactionDTO toDepositTransactionDTO(DepositTransaction depositTransaction);

    @Mapping(target = "userId", source = "user.id")
    WithdrawTransactionDTO toWithdrawTransactionDTO(WithdrawTransaction withdrawTransaction);
    @Mappings({
            @Mapping(target = "senderId", source = "sender.id"),
            @Mapping(target = "receiverId", source = "receiver.id")
    })
    PeerToPeerTransactionDTO toPeer2PeerTransactionDTO(PeerToPeerTransaction peerToPeerTransaction);
}
