package com.elleined.marketplaceapi.service.message;

import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;


// connect in /http://localhost:8083/api/v1/marketplace/ws
// subscribe to /user/private-chat/{chatRoomId} to get the private messages
// subscribe to /public-chat/topic to get public message
public interface MessageService {
    PrivateChatMessage sendPrivateMessage(
            final PrivateChatRoom privateChatRoom,
            final String message,
            final User sender
    ) throws NotValidBodyException, ResourceNotFoundException;

}
