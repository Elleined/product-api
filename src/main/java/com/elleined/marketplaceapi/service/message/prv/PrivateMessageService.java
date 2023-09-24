package com.elleined.marketplaceapi.service.message.prv;

import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.mapper.ChatMessageMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.ChatMessage;
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

import java.util.Comparator;
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
    public PrivateChatMessage getById(int privateMessageId) throws ResourceNotFoundException {
        return privateChatMessageRepository.findById(privateMessageId).orElseThrow(() -> new ResourceNotFoundException("Private message with id of " + privateMessageId + " doesn't exists!"));
    }

    @Override
    public void unsentMessage(User sender, PrivateChatRoom privateChatRoom, PrivateChatMessage privateChatMessage) throws NotOwnedException, ResourceNotFoundException {
        if (!sender.getPrivateChatMessages().contains(privateChatMessage)) throw new NotOwnedException("Cannot delete message! because you don't own these message!");
        if (!privateChatRoom.getPrivateChatMessages().contains(privateChatMessage)) throw new ResourceNotFoundException("Cannot delete message! because this private chat room doesn't have this private message!");

        privateChatMessage.setStatus(ChatMessage.Status.INACTIVE);
        privateChatMessageRepository.save(privateChatMessage);
        wsMessageService.broadCastPrivateMessage(privateChatMessage);

        log.debug("Private chat message with id of {} are now inactive", privateChatMessage.getId());
    }

    @Override
    public PrivateChatRoom getChatRoom(User sender, User receiver, Product productToSettle) throws ResourceNotFoundException {
        return productToSettle.getPrivateChatRooms().stream()
                .filter(privateChatRoom -> privateChatRoom.getSender().equals(sender) || privateChatRoom.getSender().equals(receiver))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public PrivateChatRoom getChatRoom(int roomId) throws ResourceNotFoundException {
        return privateChatRoomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Private chat room with id of " + roomId + " doesn't exists!"));
    }

    @Override
    public List<PrivateChatMessage> getAllPrivateMessage(PrivateChatRoom privateChatRoom) {
        return privateChatRoom.getPrivateChatMessages().stream()
                .filter(PrivateChatMessage::isNotDeleted)
                .sorted(Comparator.comparing(PrivateChatMessage::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public PrivateChatRoom getOrCreateChatRoom(User sender, User receiver, Product productToSettle) {
        if (hasAlreadyHaveChatRoom(sender, receiver, productToSettle))
            return getChatRoom(sender, receiver, productToSettle);

        return createPrivateChatRoom(sender, receiver, productToSettle);
    }
    private PrivateChatRoom createPrivateChatRoom(User sender, User receiver, Product productToSettle) {
        PrivateChatRoom privateChatRoom = PrivateChatRoom.privateChatRoomBuilder()
                .productToSettle(productToSettle)
                .sender(sender)
                .receiver(receiver)
                .build();

        privateChatRoomRepository.save(privateChatRoom);
        log.debug("Private chat room saved successfully with id of {}", privateChatRoom.getId());
        return privateChatRoom;
    }

    private boolean hasAlreadyHaveChatRoom(User sender, User receiver, Product productToSettle) {
        return productToSettle.getPrivateChatRooms().stream()
                .map(PrivateChatRoom::getSender)
                .anyMatch(sender::equals) ||

                productToSettle.getPrivateChatRooms().stream()
                        .map(PrivateChatRoom::getSender)
                        .anyMatch(receiver::equals) ||

                productToSettle.getPrivateChatRooms().stream()
                        .map(PrivateChatRoom::getReceiver)
                        .anyMatch(receiver::equals) ||

                productToSettle.getPrivateChatRooms().stream()
                        .map(PrivateChatRoom::getReceiver)
                        .anyMatch(sender::equals);
    }

}
