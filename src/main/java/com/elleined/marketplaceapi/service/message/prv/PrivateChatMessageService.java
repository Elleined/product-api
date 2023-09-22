package com.elleined.marketplaceapi.service.message.prv;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;

public interface PrivateChatMessageService {
    PrivateChatMessage save(PrivateChatRoom privateChatRoom, User sender, Product productToSettle, String message);
}
