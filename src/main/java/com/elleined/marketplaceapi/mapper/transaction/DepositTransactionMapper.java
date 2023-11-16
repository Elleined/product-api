package com.elleined.marketplaceapi.mapper.transaction;

import com.elleined.marketplaceapi.dto.atm.dto.DepositTransactionDTO;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DepositTransactionMapper extends TransactionMapper<DepositTransaction> {
    @Override
    @Mappings({
            @Mapping(target = "transactionFee", expression = "java(fee)"),
            @Mapping(target = "userId", source = "user.id")
    })
    DepositTransactionDTO toDTO(DepositTransaction depositTransaction, @Context float fee);
}
