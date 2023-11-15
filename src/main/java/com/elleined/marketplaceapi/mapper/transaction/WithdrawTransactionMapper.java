package com.elleined.marketplaceapi.mapper.transaction;

import com.elleined.marketplaceapi.dto.atm.dto.TransactionDTO;
import com.elleined.marketplaceapi.dto.atm.dto.WithdrawTransactionDTO;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface WithdrawTransactionMapper extends TransactionMapper<WithdrawTransaction> {
    @Override
    @Mappings({
            @Mapping(target = "userId", source = "user.id"),
            @Mapping(target = "transactionFee", expression = "java(fee)")
    })
    WithdrawTransactionDTO toDTO(WithdrawTransaction withdrawTransaction, @Context float fee);
}
