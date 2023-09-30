package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.atm.dto.DepositTransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.PeerToPeerTransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.TransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.WithdrawTransactionDTO;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.service.atm.fee.ATMFeeService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

    @Autowired @Lazy
    protected ATMFeeService atmFeeService;

    @Mappings({
            @Mapping(target = "transactionFee", expression = "java(atmFeeService.getDepositFee(depositTransaction.getAmount()))"),
            @Mapping(target = "userId", source = "user.id")
    })
    public abstract DepositTransactionDTO toDepositTransactionDTO(DepositTransaction depositTransaction);


    @Mappings({
            @Mapping(target = "userId", source = "user.id"),
            @Mapping(target = "transactionFee", expression = "java(atmFeeService.getWithdrawalFee(withdrawTransaction.getAmount()))")
    })
    public abstract WithdrawTransactionDTO toWithdrawTransactionDTO(WithdrawTransaction withdrawTransaction);
    @Mappings({
            @Mapping(target = "senderId", source = "sender.id"),
            @Mapping(target = "receiverId", source = "receiver.id"),
            @Mapping(target = "transactionFee", expression = "java(atmFeeService.getP2pFee(peerToPeerTransaction.getAmount()))")
    })
    public abstract PeerToPeerTransactionDTO toPeer2PeerTransactionDTO(PeerToPeerTransaction peerToPeerTransaction);
}
