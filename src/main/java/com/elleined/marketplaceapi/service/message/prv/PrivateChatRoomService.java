package com.elleined.marketplaceapi.service.message.prv;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;

public interface PrivateChatRoomService {
    boolean hasAlreadyHaveChatRoom(User sender, User participant, Product productToSettle);

    PrivateChatRoom getChatRoomBy(User sender, User participant, Product productToSettle) throws ResourceNotFoundException;

    PrivateChatRoom createPrivateChatRoom(User sender, User participant, Product productToSettle);
}
