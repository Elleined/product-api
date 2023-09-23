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
@RequestMapping("/users/{senderId}/private-chat")
public class PrivateMessageController {
    private final PrivateChatMessageService privateChatMessageService;
    private final PrivateChatRoomService privateChatRoomService;

    private final UserService userService;

    private final ProductService productService;

    private final ChatMessageMapper chatMessageMapper;

    @PostMapping("/{receiverId}/sendPrivateMessage")
    public PrivateChatMessageDTO sendPrivateMessage(@PathVariable("senderId") int senderId,
                                                    @PathVariable("receiverId") int receiverId,
                                                    @RequestParam("productToSettleId") int productToSettleId,
                                                    @RequestParam("message") String message) {

        User sender = userService.getById(senderId);
        User receiver = userService.getById(receiverId);
        Product productToSettle = productService.getById(productToSettleId);

        // Sends to existing private chat room else create a new chat room
        if (privateChatRoomService.hasAlreadyHaveChatRoom(sender, receiver, productToSettle)) {
            PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoomBy(sender, receiver, productToSettle);
            PrivateChatMessage privateChatMessage = privateChatMessageService.save(privateChatRoom, sender, productToSettle, message);
            return chatMessageMapper.toPrivateChatMessageDTO(privateChatMessage);
        }

        PrivateChatRoom privateChatRoom = privateChatRoomService.createPrivateChatRoom(sender, receiver, productToSettle);
        PrivateChatMessage privateChatMessage = privateChatMessageService.save(privateChatRoom, sender, productToSettle, message);
        return chatMessageMapper.toPrivateChatMessageDTO(privateChatMessage);
    }


}
