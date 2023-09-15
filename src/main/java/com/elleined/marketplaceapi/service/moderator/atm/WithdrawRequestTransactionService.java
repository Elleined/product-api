package com.elleined.marketplaceapi.service.moderator.atm;

import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Qualifier("withdrawRequestTransactionService")
public class WithdrawRequestTransactionService {

}
