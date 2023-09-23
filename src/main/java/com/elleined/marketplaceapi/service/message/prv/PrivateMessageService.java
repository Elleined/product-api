package com.elleined.marketplaceapi.service.message.prv;

import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.ChatMessageMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.message.PrivateChatMessageRepository;
import com.elleined.marketplaceapi.repository.message.PrivateChatRoomRepository;
import com.elleined.marketplaceapi.service.message.WSMessageService;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
// sender and creator are the same
public class PrivateMessageService implements PrivateChatRoomService, PrivateChatMessageService {
    private final PrivateChatMessageRepository privateChatMessageRepository;
    private final PrivateChatRoomRepository privateChatRoomRepository;

    private final WSMessageService wsMessageService;

    private final ChatMessageMapper chatMessageMapper;
    @Override
    public PrivateChatMessage save(PrivateChatRoom privateChatRoom, User sender, Product productToSettle, String message) throws NotValidBodyException {
        if (StringUtil.isNotValid(message)) throw new NotValidBodyException("Please provide your message");

        PrivateChatMessage privateChatMessage = chatMessageMapper.toPrivateChatMessageEntity(privateChatRoom, sender, message);
        privateChatMessageRepository.save(privateChatMessage);
        wsMessageService.broadCastPrivateMessage(privateChatMessage);

        log.debug("Private chat saved successfully with id of {} ", privateChatMessage.getId());
        return privateChatMessage;
    }


    @Override
    public boolean hasAlreadyHaveChatRoom(User sender, User participant, Product productToSettle) {
        return productToSettle.getPrivateChatRooms().stream()
                .map(PrivateChatRoom::getSender)
                .anyMatch(sender::equals) ||

        productToSettle.getPrivateChatRooms().stream()
                .map(PrivateChatRoom::getSender)
                .anyMatch(participant::equals) ||

        productToSettle.getPrivateChatRooms().stream()
                .map(PrivateChatRoom::getParticipant)
                .anyMatch(participant::equals) ||

        productToSettle.getPrivateChatRooms().stream()
                .map(PrivateChatRoom::getParticipant)
                .anyMatch(sender::equals);
    }

    @Override
    public PrivateChatRoom getChatRoomBy(User sender, User participant, Product productToSettle) throws ResourceNotFoundException {
        return productToSettle.getPrivateChatRooms().stream()
                .filter(privateChatRoom -> privateChatRoom.getSender().equals(sender) || privateChatRoom.getSender().equals(participant))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public PrivateChatRoom createPrivateChatRoom(User sender, User participant, Product productToSettle) {
        PrivateChatRoom privateChatRoom = PrivateChatRoom.privateChatRoomBuilder()
                .productToSettle(productToSettle)
                .sender(sender)
                .participant(participant)
                .build();

        privateChatRoomRepository.save(privateChatRoom);
        log.debug("Private chat room saved successfully with id of {}", privateChatRoom.getId());
        return privateChatRoom;
    }
}
