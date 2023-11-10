//package com.elleined.marketplaceapi.controller;
//
//import com.elleined.marketplaceapi.dto.message.PrivateChatMessageDTO;
//import com.elleined.marketplaceapi.mapper.ChatMessageMapper;
//import com.elleined.marketplaceapi.model.product.Product;
//import com.elleined.marketplaceapi.model.message.ChatRoom;
//import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
//import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
//import com.elleined.marketplaceapi.model.user.User;
//import com.elleined.marketplaceapi.service.message.prv.PrivateChatMessageService;
//import com.elleined.marketplaceapi.service.message.prv.PrivateChatRoomService;
//import com.elleined.marketplaceapi.service.product.ProductService;
//import com.elleined.marketplaceapi.service.user.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/users/{senderId}/private/chat-rooms")
//public class PrivateMessageController {
//    private final PrivateChatMessageService privateChatMessageService;
//    private final PrivateChatRoomService privateChatRoomService;
//
//    private final UserService userService;
//
//    private final ProductService productService;
//
//    private final ChatMessageMapper chatMessageMapper;
//
//    @PostMapping("/{roomId}")
//    public PrivateChatMessageDTO sendPrivateMessage(@PathVariable("senderId") int senderId,
//                                                    @PathVariable("roomId") int roomId,
//                                                    @RequestParam("productToSettleId") int productToSettleId,
//                                                    @RequestPart(value = "picture", required = false) MultipartFile picture,
//                                                    @RequestParam("message") String message) throws IOException {
//
//        User sender = userService.getById(senderId);
//        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
//        Product productToSettle = productService.getById(productToSettleId);
//
//        PrivateChatMessage privateChatMessage = privateChatMessageService.save(privateChatRoom, sender, productToSettle, picture, message);
//        return chatMessageMapper.toPrivateChatMessageDTO(privateChatMessage);
//    }
//
//    @GetMapping("/receiver/{receiverId}/product/{productToSettleId}")
//    public int getOrCreateChatRoom(@PathVariable("senderId") int senderId,
//                                   @PathVariable("receiverId") int receiverId,
//                                   @PathVariable("productToSettleId") int productToSettleId) {
//
//        User sender = userService.getById(senderId);
//        User receiver = userService.getById(receiverId);
//        Product productToSettle = productService.getById(productToSettleId);
//
//        return privateChatRoomService.getOrCreateChatRoom(sender, receiver, productToSettle).getId();
//    }
//
//
//    @DeleteMapping("/{roomId}/messages/{messageId}")
//    public void unsentPrivateMessage(@PathVariable("senderId") int senderId,
//                                     @PathVariable("roomId") int roomId,
//                                     @PathVariable("messageId") int messageId) {
//
//        User sender = userService.getById(senderId);
//        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
//        PrivateChatMessage privateChatMessage = privateChatMessageService.getById(messageId);
//
//        privateChatMessageService.unsentMessage(sender, privateChatRoom, privateChatMessage);
//    }
//
//    @GetMapping("/{roomId}/messages")
//    public List<PrivateChatMessageDTO> getAllMessage(@PathVariable("roomId") int roomId) {
//        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
//        return privateChatRoomService.getAllPrivateMessage(privateChatRoom).stream()
//                .map(chatMessageMapper::toPrivateChatMessageDTO)
//                .toList();
//    }
//
//    @PatchMapping("/{roomId}/accept-agreement")
//    public void acceptAgreement(@PathVariable("senderId") int senderId,
//                                @PathVariable("roomId") int roomId) {
//        User user = userService.getById(senderId);
//        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
//        privateChatRoomService.acceptAgreement(user, privateChatRoom);
//    }
//
//    @GetMapping("/{roomId}/agreement-status")
//    public ChatRoom.Status getPrivateChatRoomStatus(@PathVariable("senderId") int senderId,
//                                                    @PathVariable("roomId") int roomId) {
//        User user = userService.getById(senderId);
//        PrivateChatRoom privateChatRoom = privateChatRoomService.getChatRoom(roomId);
//        return privateChatRoomService.getStatus(user, privateChatRoom);
//    }
//}
