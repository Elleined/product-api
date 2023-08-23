package com.elleined.marketplaceapi.service.message;

import com.elleined.marketplaceapi.dto.Message;
import com.elleined.marketplaceapi.dto.PrivateMessage;
import com.elleined.marketplaceapi.exception.NoLoggedInUserException;
import com.elleined.marketplaceapi.exception.NotValidBodyException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;


// connect in /http://localhost:8083/api/v1/marketplace/ws
// subscribe to /user/private-chat to get the private messages
// subscribe to /public-chat/topic to get public message
public interface MessageService {

    PrivateMessage sendPrivateMessage(int recipientId, String message)
            throws NoLoggedInUserException, NotValidBodyException, ResourceNotFoundException;


    Message sendPublicMessage(String message);
}
