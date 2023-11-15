package com.elleined.marketplaceapi.mapper.transaction;

import com.elleined.marketplaceapi.dto.atm.dto.PeerToPeerTransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.TransactionDTO;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface P2PTransactionMapper extends TransactionMapper<PeerToPeerTransaction> {

    @Override
    @Mappings({
            @Mapping(target = "senderId", source = "sender.id"),
            @Mapping(target = "receiverId", source = "receiver.id"),
            @Mapping(target = "transactionFee", expression = "java(fee)")
    })
    PeerToPeerTransactionDTO toDTO(PeerToPeerTransaction peerToPeerTransaction, @Context float fee);
}
