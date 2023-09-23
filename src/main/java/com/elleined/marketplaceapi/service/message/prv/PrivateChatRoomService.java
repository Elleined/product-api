package com.elleined.marketplaceapi.service.message.prv;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface PrivateChatRoomService {
    boolean hasAlreadyHaveChatRoom(User sender, User receiver, Product productToSettle);

    PrivateChatRoom getChatRoom(User sender, User receiver, Product productToSettle) throws ResourceNotFoundException;

    PrivateChatRoom getChatRoom(int roomId) throws ResourceNotFoundException;
    PrivateChatRoom createPrivateChatRoom(User sender, User receiver, Product productToSettle);

    List<PrivateChatMessage> getAllPrivateMessage(PrivateChatRoom privateChatRoom);
}
