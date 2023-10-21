package com.elleined.marketplaceapi.service.message.prv;

import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.message.MessageAgreementNotAcceptedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PrivateChatMessageService {
    PrivateChatMessage save(PrivateChatRoom privateChatRoom, User sender, Product productToSettle, MultipartFile picture, String message)
            throws NotValidBodyException,
            MessageAgreementNotAcceptedException, IOException;

    PrivateChatMessage getById(int privateMessageId)
            throws ResourceNotFoundException;

    void unsentMessage(User sender, PrivateChatRoom privateChatRoom, PrivateChatMessage privateChatMessage)
            throws NotOwnedException, ResourceNotFoundException;
}
