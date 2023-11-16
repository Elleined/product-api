package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.message.PrivateChatMessageDTO;
import com.elleined.marketplaceapi.mapper.PrivateChatMessageMapper;
import com.elleined.marketplaceapi.model.message.ChatRoom;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.product.ProductRepository;
import com.elleined.marketplaceapi.service.message.prv.PrivateChatMessageService;
import com.elleined.marketplaceapi.service.message.prv.PrivateChatRoomService;
import com.elleined.marketplaceapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{senderId}/private/chat-rooms")
public class PrivateMessageController {
    private final PrivateChatMessageService privateChatMessageService;
    private final PrivateChatRoomService privateChatRoomService;

    private final UserService userService;

    private final ProductRepository productRepository;

    private final PrivateChatMessageMapper privateChatMessageMapper;

    @PostMapping("/{roomId}")
    public PrivateChatMessageDTO sendPrivateMessage(@PathVariable("senderId") int senderId,
                                                    @PathVariable("roomId") int roomId,
                                                    @RequestPart(value = "picture", required = false) MultipartFile picture,
                                                    @RequestParam("message") String message) throws IOException {

        User sender = userService.getById(senderId);
        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);

        PrivateChatMessage privateChatMessage = privateChatMessageService.save(privateChatRoom, sender, picture, message);
        return privateChatMessageMapper.toDTO(privateChatMessage);
    }

    @GetMapping("/receiver/{receiverId}/product/{productToSettleId}")
    public int getOrCreateChatRoom(@PathVariable("senderId") int senderId,
                                   @PathVariable("receiverId") int receiverId,
                                   @PathVariable("productToSettleId") int productToSettleId) {

        User sender = userService.getById(senderId);
        User receiver = userService.getById(receiverId);
        Product productToSettle = productRepository.findById(productToSettleId).orElseThrow();

        return privateChatRoomService.getOrCreateChatRoom(sender, receiver, productToSettle).getId();
    }


    @DeleteMapping("/{roomId}/messages/{messageId}")
    public void unsentPrivateMessage(@PathVariable("senderId") int senderId,
                                     @PathVariable("roomId") int roomId,
                                     @PathVariable("messageId") int messageId) {

        User sender = userService.getById(senderId);
        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
        PrivateChatMessage privateChatMessage = privateChatMessageService.getById(messageId);

        privateChatMessageService.unsentMessage(sender, privateChatRoom, privateChatMessage);
    }

    @GetMapping("/{roomId}/messages")
    public List<PrivateChatMessageDTO> getAllMessage(@PathVariable("roomId") int roomId) {
        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
        return privateChatRoomService.getAllPrivateMessage(privateChatRoom).stream()
                .map(privateChatMessageMapper::toDTO)
                .toList();
    }

    @PatchMapping("/{roomId}/accept-agreement")
    public void acceptAgreement(@PathVariable("senderId") int senderId,
                                @PathVariable("roomId") int roomId) {
        User user = userService.getById(senderId);
        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
        privateChatRoomService.acceptAgreement(user, privateChatRoom);
    }

    @GetMapping("/{roomId}/agreement-status")
    public ChatRoom.Status getPrivateChatRoomStatus(@PathVariable("senderId") int senderId,
                                                    @PathVariable("roomId") int roomId) {
        User user = userService.getById(senderId);
        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
        return privateChatRoomService.getStatus(user, privateChatRoom);
    }
}
