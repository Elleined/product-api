package com.elleined.marketplaceapi.service.message.prv;

import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.exception.message.MessageAgreementNotAcceptedException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.mapper.ChatMessageMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.ChatMessage;
import com.elleined.marketplaceapi.model.message.ChatRoom;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.message.PrivateChatMessageRepository;
import com.elleined.marketplaceapi.repository.message.PrivateChatRoomRepository;
import com.elleined.marketplaceapi.service.image.ImageUploader;
import com.elleined.marketplaceapi.service.message.WSMessageService;
import com.elleined.marketplaceapi.service.validator.Validator;
import com.elleined.marketplaceapi.utils.DirectoryFolders;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PrivateMessageService implements PrivateChatRoomService, PrivateChatMessageService {
    private final PrivateChatMessageRepository privateChatMessageRepository;
    private final PrivateChatRoomRepository privateChatRoomRepository;

    private final WSMessageService wsMessageService;

    private final ChatMessageMapper chatMessageMapper;

    private final ImageUploader imageUploader;

    @Value("${cropTrade.img.directory}")
    private String cropTradeImgDirectory;

    @Override
    public PrivateChatMessage save(PrivateChatRoom privateChatRoom, User currentUser, Product productToSettle, MultipartFile picture, String message) throws NotValidBodyException, MessageAgreementNotAcceptedException, IOException {
        if (privateChatRoom.getSender().equals(currentUser) && privateChatRoom.getIsSenderAcceptedAgreement() == ChatRoom.Status.NOT_ACCEPTED) throw new MessageAgreementNotAcceptedException("Cannot send private message! because you don't accept our chat agreement!");
        if (privateChatRoom.getReceiver().equals(currentUser) && privateChatRoom.getIsReceiverAcceptedAgreement() == ChatRoom.Status.NOT_ACCEPTED) throw new MessageAgreementNotAcceptedException("Cannot send private message! because you don't accept our chat agreement!");
        if (StringUtil.isNotValid(message)) throw new NotValidBodyException("Please provide your message");

        String pictureImage = Validator.validMultipartFile(picture) ? picture.getOriginalFilename() : null;
        PrivateChatMessage privateChatMessage = chatMessageMapper.toPrivateChatMessageEntity(privateChatRoom, currentUser, pictureImage, message);
        privateChatMessageRepository.save(privateChatMessage);
        wsMessageService.broadCastPrivateMessage(privateChatMessage);

        if (Validator.validMultipartFile(picture)) imageUploader.upload(cropTradeImgDirectory + DirectoryFolders.PRIVATE_CHAT_FOLDER, picture);
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
    public void acceptAgreement(User user, PrivateChatRoom privateChatRoom) throws NotOwnedException {
        if (privateChatRoom.getSender().equals(user)) privateChatRoom.setIsSenderAcceptedAgreement(ChatRoom.Status.ACCEPTED);
        else if (privateChatRoom.getReceiver().equals(user)) privateChatRoom.setIsReceiverAcceptedAgreement(ChatRoom.Status.ACCEPTED);
        else throw new NotOwnedException("Cannot accept chat agreement! because user with id of " + user.getId() + " doesn't have this private chat!");
        privateChatRoomRepository.save(privateChatRoom);
        log.debug("User with id of {} acccepted chat agreement", user.getId());
    }

    @Override
    public ChatRoom.Status getStatus(User user, PrivateChatRoom privateChatRoom) throws NotOwnedException {
        if (privateChatRoom.getReceiver().equals(user)) return privateChatRoom.getIsReceiverAcceptedAgreement();
        else if (privateChatRoom.getSender().equals(user)) return privateChatRoom.getIsSenderAcceptedAgreement();
        else throw new NotOwnedException("Cannot get status chat agreement! because user with id of " + user.getId() + " doesn't have this private chat!");
    }

    @Override
    public List<PrivateChatMessage> getAllPrivateMessage(PrivateChatRoom privateChatRoom) {
        return privateChatRoom.getPrivateChatMessages().stream()
                .filter(PrivateChatMessage::isNotDeleted)
                .sorted(Comparator.comparing(PrivateChatMessage::getCreatedAt))
                .toList();
    }

    @Override
    public PrivateChatRoom getOrCreateChatRoom(User sender, User receiver, Product productToSettle) {
        if (hasAlreadyHaveChatRoom(sender, receiver, productToSettle)) return getChatRoom(sender, receiver, productToSettle);
        return createPrivateChatRoom(sender, receiver, productToSettle);
    }
    private PrivateChatRoom createPrivateChatRoom(User sender, User receiver, Product productToSettle) {
        PrivateChatRoom privateChatRoom = PrivateChatRoom.privateChatRoomBuilder()
                .productToSettle(productToSettle)
                .isReceiverAcceptedAgreement(ChatRoom.Status.NOT_ACCEPTED)
                .isSenderAcceptedAgreement(ChatRoom.Status.NOT_ACCEPTED)
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
