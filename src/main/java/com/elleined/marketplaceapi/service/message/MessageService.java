package com.elleined.marketplaceapi.service.message;

import com.elleined.marketplaceapi.dto.Message;
import com.elleined.marketplaceapi.dto.PrivateMessage;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.NoLoggedInUserException;


// connect in /http://localhost:8083/api/v1/marketplace/ws
// subscribe to /user/private-chat/{chatRoomId} to get the private messages
// subscribe to /public-chat/topic to get public message
public interface MessageService {

    PrivateMessage sendPrivateMessage(int recipientId, String message)
            throws NoLoggedInUserException, NotValidBodyException, ResourceNotFoundException;


    Message sendPublicMessage(String message)
            throws NotValidBodyException, NoLoggedInUserException;
}
