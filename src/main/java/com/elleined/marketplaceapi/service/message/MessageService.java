package com.elleined.marketplaceapi.service.message;

import com.elleined.marketplaceapi.dto.Message;
import com.elleined.marketplaceapi.exception.NoLoggedInUserException;
import com.elleined.marketplaceapi.exception.NotValidBodyException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;


// connect in /http://localhost:8083/api/v1/marketplace/ws
// subscribe to /user/private-chat
public interface MessageService {

    Message sendPrivateMessage(int recipientId, String message)
            throws NoLoggedInUserException, NotValidBodyException, ResourceNotFoundException;

    
}
