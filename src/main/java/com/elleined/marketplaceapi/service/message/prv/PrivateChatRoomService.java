package com.elleined.marketplaceapi.service.message.prv;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;

public interface PrivateChatRoomService {
    boolean hasAlreadyHaveConversation(User sender, Product productToSettle);

    PrivateChatRoom getById(int privateChatId) throws ResourceNotFoundException;

    PrivateChatRoom createPrivateChatRoom(Product productToSettle);
}
