package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.message.PrivateChatMessageDTO;
import com.elleined.marketplaceapi.mapper.ChatMessageMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.message.prv.PrivateChatMessageService;
import com.elleined.marketplaceapi.service.message.prv.PrivateChatRoomService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{senderId}/private")
public class PrivateMessageController {
    private final PrivateChatMessageService privateChatMessageService;
    private final PrivateChatRoomService privateChatRoomService;

    private final UserService userService;

    private final ProductService productService;

    private final ChatMessageMapper chatMessageMapper;

    @PostMapping("/chat-rooms/{roomId}")
    public PrivateChatMessageDTO sendPrivateMessage(@PathVariable("senderId") int senderId,
                                                    @PathVariable("roomId") int roomId,
                                                    @RequestParam("productToSettleId") int productToSettleId,
                                                    @RequestParam("message") String message) {

        User sender = userService.getById(senderId);
        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
        Product productToSettle = productService.getById(productToSettleId);

        PrivateChatMessage privateChatMessage = privateChatMessageService.save(privateChatRoom, sender, productToSettle, message);
        return chatMessageMapper.toPrivateChatMessageDTO(privateChatMessage);
    }

    @GetMapping("/chat-rooms/receiver/{receiverId}/product/{productToSettleId}")
    public int getOrCreateChatRoom(@PathVariable("senderId") int senderId,
                             @PathVariable("receiverId") int receiverId,
                             @PathVariable("productToSettleId") int productToSettleId) {

        User sender = userService.getById(senderId);
        User receiver = userService.getById(receiverId);
        Product productToSettle = productService.getById(productToSettleId);

        return privateChatRoomService.getOrCreateChatRoom(sender, receiver, productToSettle).getId();
    }


    @DeleteMapping("/chat-rooms/{roomId}/messages/{messageId}")
    public void unsentPrivateMessage(@PathVariable("senderId") int senderId,
                                     @PathVariable("roomId") int roomId,
                                     @PathVariable("messageId") int messageId) {

        User sender = userService.getById(senderId);
        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
        PrivateChatMessage privateChatMessage = privateChatMessageService.getById(messageId);

        privateChatMessageService.unsentMessage(sender, privateChatRoom, privateChatMessage);
    }

    @GetMapping("/chat-rooms/{roomId}/messages")
    private List<PrivateChatMessageDTO> getAllMessage(@PathVariable("roomId") int roomId) {
        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
        return privateChatRoomService.getAllPrivateMessage(privateChatRoom).stream()
                .map(chatMessageMapper::toPrivateChatMessageDTO)
                .toList();
    }
}
