package com.elleined.marketplaceapi.model.message.prv;


import com.elleined.marketplaceapi.model.message.ChatMessage;
import com.elleined.marketplaceapi.model.message.ChatRoom;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.Entity;
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

    @Builder(builderMethodName = "privateChatMessageBuilder")
    public PrivateChatMessage(int id, String message, ChatRoom chatRoom, User sender, Status status) {
        super(id, message, chatRoom, sender, status);
    }
}
