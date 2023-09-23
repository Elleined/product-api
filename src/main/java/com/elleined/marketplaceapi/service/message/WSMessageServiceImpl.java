package com.elleined.marketplaceapi.service.message;

import com.elleined.marketplaceapi.dto.message.PrivateChatMessageDTO;
import com.elleined.marketplaceapi.mapper.ChatMessageMapper;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WSMessageServiceImpl implements WSMessageService {
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChatMessageMapper chatMessageMapper;

    @Override
    public void broadCastPrivateMessage(PrivateChatMessage privateChatMessage) {
        PrivateChatRoom privateChatRoom = privateChatMessage.getPrivateChatRoom();
        PrivateChatMessageDTO privateChatMessageDTO = chatMessageMapper.toPrivateChatMessageDTO(privateChatMessage);

        final String privateChatRoomId = String.valueOf(privateChatRoom.getId());
        final String destination = "/private/chat-rooms/" + privateChatRoomId;
        simpMessagingTemplate.convertAndSend(destination, privateChatMessageDTO);
        log.debug("Private message successfully broadcasted to {} to chat room with id of {}", destination, privateChatRoomId);
    }
}
