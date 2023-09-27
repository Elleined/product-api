package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.message.PrivateChatMessageDTO;
import com.elleined.marketplaceapi.model.message.ChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", imports = {ChatMessage.Status.class})
public interface ChatMessageMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "privateChatRoom", expression = "java(privateChatRoom)"),
            @Mapping(target = "message", expression = "java(message)"),
            @Mapping(target = "sender", expression = "java(currentUser)"),
            @Mapping(target = "status", expression = "java(Status.ACTIVE)"),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    })
    PrivateChatMessage toPrivateChatMessageEntity(PrivateChatRoom privateChatRoom,
                                                  @Context User currentUser,
                                                  @Context String message);

    @Mappings({
            @Mapping(target = "privateRoomId", source = "privateChatRoom.id"),
            @Mapping(target = "productToSettleId", source = "privateChatRoom.productToSettle.id"),
            @Mapping(target = "senderId", source = "sender.id")
    })
    PrivateChatMessageDTO toPrivateChatMessageDTO(PrivateChatMessage privateChatMessage);
}
