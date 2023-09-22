package com.elleined.marketplaceapi.service.message;

import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.message.PrivateChatMessageRepository;
import com.elleined.marketplaceapi.repository.message.PrivateChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final PrivateChatRoomRepository privateChatRoomRepository;
    private final PrivateChatMessageRepository privateChatMessageRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public PrivateChatMessage sendPrivateMessage(PrivateChatRoom privateChatRoom, String message, User sender) throws NotValidBodyException, ResourceNotFoundException {
        return null;
    }
}
