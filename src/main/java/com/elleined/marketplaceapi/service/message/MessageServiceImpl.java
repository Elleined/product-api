package com.elleined.marketplaceapi.service.message;

import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.ChatMessageMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.message.ChatMessageRepository;
import com.elleined.marketplaceapi.repository.message.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChatMessageMapper chatMessageMapper;

    @Override
    public PrivateChatMessage sendPrivateMessage(User sender, Product productToSettle, String message) throws NotValidBodyException, ResourceNotFoundException {
        PrivateChatMessage privateChatMessage = this.sendPrivateMessage(productToSettle, sender, message);
        // if not exist create new chat room else retrieve the previous

        final String chatRoomId = String.valueOf(privateChatMessage.getPrivateChatRoom().getId());
        final String destination = "/private-chat/" + chatRoomId;
        simpMessagingTemplate.convertAndSendToUser(chatRoomId, destination, privateChatMessage);
        return privateChatMessage;
    }

    public PrivateChatMessage sendPrivateMessage(Product productToSettle, User sender, String message) {
        PrivateChatRoom privateChatRoom = PrivateChatRoom.privateChatRoomBuilder()
                .productToSettle(productToSettle)
                .privateChatMessages(new ArrayList<>())
                .build();
        PrivateChatMessage privateChatMessage = chatMessageMapper.toPrivateChatMessageEntity(privateChatRoom, sender, message);
        chatRoomRepository.save(privateChatRoom);
        chatMessageRepository.save(privateChatMessage);
        return privateChatMessage;
    }
}
