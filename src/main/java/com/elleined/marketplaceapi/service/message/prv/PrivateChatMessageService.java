package com.elleined.marketplaceapi.service.message.prv;

import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.message.MessageAgreementNotAcceptedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;

public interface PrivateChatMessageService {
    PrivateChatMessage save(PrivateChatRoom privateChatRoom, User sender, Product productToSettle, String message)
            throws NotValidBodyException, MessageAgreementNotAcceptedException;

    PrivateChatMessage getById(int privateMessageId)
            throws ResourceNotFoundException;

    void unsentMessage(User sender, PrivateChatRoom privateChatRoom, PrivateChatMessage privateChatMessage)
            throws NotOwnedException, ResourceNotFoundException;
}
