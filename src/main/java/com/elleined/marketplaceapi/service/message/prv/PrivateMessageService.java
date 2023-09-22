package com.elleined.marketplaceapi.service.message.prv;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.ChatMessageMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.message.PrivateChatMessageRepository;
import com.elleined.marketplaceapi.repository.message.PrivateChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PrivateMessageService implements PrivateChatRoomService, PrivateChatMessageService {
    private final PrivateChatMessageRepository privateChatMessageRepository;
    private final PrivateChatRoomRepository privateChatRoomRepository;

    private final ChatMessageMapper chatMessageMapper;
    @Override
    public PrivateChatMessage save(PrivateChatRoom privateChatRoom, User sender, Product productToSettle, String message) {
        PrivateChatMessage privateChatMessage = chatMessageMapper.toPrivateChatMessageEntity(privateChatRoom, sender, message);
        privateChatMessageRepository.save(privateChatMessage);
        log.debug("Private chat saved successfully with id of {} ", privateChatMessage.getId());
        return privateChatMessage;
    }

    @Override
    public boolean hasAlreadyHaveConversation(User sender, Product productToSettle) {
        return productToSettle.getPrivateChatRooms().stream()
                .map(PrivateChatRoom::getPrivateChatMessages)
                .flatMap(Collection::stream)
                .map(PrivateChatMessage::getSender)
                .anyMatch(sender::equals);
    }

    @Override
    public PrivateChatRoom getById(int privateChatId) throws ResourceNotFoundException {
        return privateChatRoomRepository.findById(privateChatId).orElseThrow(() -> new ResourceNotFoundException("Private chat room with id of " + privateChatId + " does not exists!"));
    }

    @Override
    public PrivateChatRoom save(Product productToSettle) {
        PrivateChatRoom privateChatRoom = PrivateChatRoom.privateChatRoomBuilder()
                .productToSettle(productToSettle)
                .build();

        privateChatRoomRepository.save(privateChatRoom);
        log.debug("Private chat room saved successfully with id of {}", privateChatRoom.getId());
        return privateChatRoom;
    }
}
