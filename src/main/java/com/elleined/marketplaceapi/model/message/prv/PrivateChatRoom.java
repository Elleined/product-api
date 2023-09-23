package com.elleined.marketplaceapi.model.message.prv;


import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.ChatMessage;
import com.elleined.marketplaceapi.model.message.ChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tbl_private_chat_room")
@NoArgsConstructor
@Getter
@Setter
public class PrivateChatRoom extends ChatRoom {

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "product_to_settle_id",
            referencedColumnName = "product_id",
            nullable = false,
            updatable = false
    )
    private Product productToSettle;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "sender_id",
            referencedColumnName = "user_id",
            nullable = false,
            updatable = false
    )
    private User sender;


    @ManyToOne(optional = false)
    @JoinColumn(
            name = "receiver_id",
            referencedColumnName = "user_id",
            nullable = false,
            updatable = false
    )
    private User receiver;

    // chat room id reference is in tbl private chat message table
    @OneToMany(mappedBy = "privateChatRoom")
    private List<PrivateChatMessage> privateChatMessages;

    @Builder(builderMethodName = "privateChatRoomBuilder")
    public PrivateChatRoom(int id, Product productToSettle, User sender, User receiver, List<PrivateChatMessage> privateChatMessages) {
        super(id);
        this.productToSettle = productToSettle;
        this.sender = sender;
        this.receiver = receiver;
        this.privateChatMessages = privateChatMessages;
    }
}
