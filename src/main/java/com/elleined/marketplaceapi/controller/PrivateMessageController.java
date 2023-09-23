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
@RequestMapping("/users/{senderId}/private-chat")
public class PrivateMessageController {
    private final PrivateChatMessageService privateChatMessageService;
    private final PrivateChatRoomService privateChatRoomService;

    private final UserService userService;

    private final ProductService productService;

    private final ChatMessageMapper chatMessageMapper;

    @PostMapping("/receiver/{receiverId}/sendPrivateMessage")
    public PrivateChatMessageDTO sendPrivateMessage(@PathVariable("senderId") int senderId,
                                                    @PathVariable("receiverId") int receiverId,
                                                    @RequestParam("productToSettleId") int productToSettleId,
                                                    @RequestParam("message") String message) {

        User sender = userService.getById(senderId);
        User receiver = userService.getById(receiverId);
        Product productToSettle = productService.getById(productToSettleId);

        // Sends to existing private chat room else create a new chat room
        if (privateChatRoomService.hasAlreadyHaveChatRoom(sender, receiver, productToSettle)) {
            PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(sender, receiver, productToSettle);
            PrivateChatMessage privateChatMessage = privateChatMessageService.save(privateChatRoom, sender, productToSettle, message);
            return chatMessageMapper.toPrivateChatMessageDTO(privateChatMessage);
        }

        PrivateChatRoom privateChatRoom = privateChatRoomService.createPrivateChatRoom(sender, receiver, productToSettle);
        PrivateChatMessage privateChatMessage = privateChatMessageService.save(privateChatRoom, sender, productToSettle, message);
        return chatMessageMapper.toPrivateChatMessageDTO(privateChatMessage);
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
