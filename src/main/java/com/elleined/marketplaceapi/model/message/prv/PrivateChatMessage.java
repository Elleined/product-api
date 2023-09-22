package com.elleined.marketplaceapi.model.message.prv;


import com.elleined.marketplaceapi.model.message.ChatMessage;
import com.elleined.marketplaceapi.model.message.ChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_private_chat_message")
@NoArgsConstructor
@Getter
@Setter
public class PrivateChatMessage extends ChatMessage {

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "private_chat_room_id",
            referencedColumnName = "chat_room_id",
            nullable = false,
            updatable = false
    )
    private PrivateChatRoom privateChatRoom;

    @Builder(builderMethodName = "privateMessageBuilder")
    public PrivateChatMessage(int id, String message, User sender, Status status, PrivateChatRoom privateChatRoom) {
        super(id, message, sender, status);
        this.privateChatRoom = privateChatRoom;
    }
}
