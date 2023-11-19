package com.elleined.marketplaceapi.service.moderator.request.transaction;

import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.service.moderator.request.Request;

public interface TransactionRequest<T extends Transaction> extends Request<T> {
}
