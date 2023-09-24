package com.elleined.marketplaceapi.service.message;

import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;


// connect in /http://localhost:8083/api/v1/marketplace/ws
// subscribe to /user/private-chat/{chatRoomId} to get the private messages
// subscribe to /public-chat/topic to get public message
public interface WSMessageService {
    void broadCastPrivateMessage(PrivateChatMessage privateChatMessage);
}
