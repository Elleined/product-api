package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.message.PrivateChatMessageDTO;
import com.elleined.marketplaceapi.model.message.ChatMessage;
import com.elleined.marketplaceapi.model.message.ChatRoom;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.elleined.marketplaceapi.model.message.ChatMessage.Status.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PrivateChatMessageMapperTest {

    @InjectMocks
    private PrivateChatMessageMapper privateChatMessageMapper = Mappers.getMapper(PrivateChatMessageMapper.class);

    @Test
    void toEntity() {
        Product retailProduct = RetailProduct.retailProductBuilder()
                .id(1)
                .build();

        User currentUser = User.builder()
                .id(1)
                .build();

        PrivateChatRoom privateChatRoom = PrivateChatRoom.privateChatRoomBuilder()
                .id(1)
                .isSenderAcceptedAgreement(ChatRoom.Status.NOT_ACCEPTED)
                .isReceiverAcceptedAgreement(ChatRoom.Status.NOT_ACCEPTED)
                .productToSettle(retailProduct)
                .sender(currentUser)
                .receiver(User.builder()
                        .id(2)
                        .build())
                .privateChatMessages(new ArrayList<>())
                .build();

        PrivateChatMessage actual = privateChatMessageMapper.toEntity(privateChatRoom, currentUser, "PIcture", "Message");

        assertEquals(0, actual.getId());

        assertNotNull(actual.getMessage());
        assertNotNull(actual.getPicture());

        assertNotNull(actual.getSender());
        assertEquals(currentUser, actual.getSender());

        assertNotNull(actual.getCreatedAt());

        assertEquals(ACTIVE, actual.getStatus());
        assertNotNull(actual.getStatus());

        assertEquals(privateChatRoom, actual.getPrivateChatRoom());
        assertNotNull(actual.getPrivateChatRoom());
    }

    @Test
    void toDTO() {
        PrivateChatRoom privateChatRoom = PrivateChatRoom.privateChatRoomBuilder()
                .id(1)
                .isSenderAcceptedAgreement(ChatRoom.Status.NOT_ACCEPTED)
                .isReceiverAcceptedAgreement(ChatRoom.Status.NOT_ACCEPTED)
                .productToSettle(RetailProduct.retailProductBuilder()
                        .id(1)
                        .build())
                .sender(User.builder()
                        .id(1)
                        .build())
                .receiver(User.builder()
                        .id(2)
                        .build())
                .privateChatMessages(new ArrayList<>())
                .build();

        PrivateChatMessage expected = PrivateChatMessage.privateMessageBuilder()
                .id(1)
                .message("Message")
                .picture("Picture")
                .sender(User.builder()
                        .id(1)
                        .build())
                .createdAt(LocalDateTime.now())
                .status(ACTIVE)
                .privateChatRoom(privateChatRoom)
                .build();

        PrivateChatMessageDTO actual = privateChatMessageMapper.toDTO(expected);

        assertNotNull(actual.getMessage());
        assertEquals(expected.getMessage(), actual.getMessage());

        assertEquals(expected.getSender().getId(), actual.getSenderId());

        assertNotNull(actual.getStatus());
        assertEquals(expected.getStatus().name(), actual.getStatus());

        assertNotNull(actual.getCreatedAt());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());

        assertNotNull(actual.getPicture());
        assertEquals(expected.getPicture(), actual.getPicture());

        assertEquals(expected.getId(), actual.getId());

        assertEquals(expected.getPrivateChatRoom().getId(), actual.getPrivateRoomId());

        assertEquals(expected.getPrivateChatRoom().getProductToSettle().getId(), actual.getProductToSettleId());
    }
}