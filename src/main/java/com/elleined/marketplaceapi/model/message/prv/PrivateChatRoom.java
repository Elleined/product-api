package com.elleined.marketplaceapi.model.message.prv;


import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.message.ChatMessage;
import com.elleined.marketplaceapi.model.message.ChatRoom;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @Builder(builderMethodName = "privateChatRoomBuilder")
    public PrivateChatRoom(int id, List<ChatMessage> chatMessages, Product productToSettle) {
        super(id, chatMessages);
        this.productToSettle = productToSettle;
    }
}
