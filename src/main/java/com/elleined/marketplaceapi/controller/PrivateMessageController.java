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

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{senderId}/rooms/{privateChatRoomId}/private-chat")
public class PrivateMessageController {
    private final PrivateChatMessageService privateChatMessageService;
    private final PrivateChatRoomService privateChatRoomService;

    private final UserService userService;

    private final ProductService productService;

    private final ChatMessageMapper chatMessageMapper;

    @PostMapping
    public PrivateChatMessageDTO sendPrivateMessage(@PathVariable("senderId") int senderId,
                                                    @PathVariable("privateChatRoomId") int privateChatRoomId,
                                                    @RequestParam("productToSettleId") int productToSettleId,
                                                    @RequestParam("message") String message) {

        User sender = userService.getById(senderId);
        Product productToSettle = productService.getById(productToSettleId);

        // Sends to existing chat room else create a new chat room
        if (privateChatRoomService.hasAlreadyHaveConversation(sender, productToSettle)) {
            PrivateChatRoom privateChatRoom = privateChatRoomService.getById(privateChatRoomId);
            PrivateChatMessage privateChatMessage = privateChatMessageService.save(privateChatRoom, sender, productToSettle, message);
            return chatMessageMapper.toPrivateChatMessageDTO(privateChatMessage);
        }

        PrivateChatRoom privateChatRoom = privateChatRoomService.createPrivateChatRoom(productToSettle);
        PrivateChatMessage privateChatMessage = privateChatMessageService.save(privateChatRoom, sender, productToSettle, message);
        return chatMessageMapper.toPrivateChatMessageDTO(privateChatMessage);
    }
}
