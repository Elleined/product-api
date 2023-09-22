package com.elleined.marketplaceapi.model.message;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "tbl_chat_room")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ChatRoom {

    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "chatRoomAutoIncrement"
    )
    @SequenceGenerator(
            allocationSize = 1,
            name = "chatRoomAutoIncrement",
            sequenceName = "chatRoomAutoIncrement"
    )
    @Column(
            name = "chat_room_id",
            nullable = false,
            updatable = false,
            unique = true
    )
    private int id;

    // Chat room id reference is in tbl_chat_room_message
    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> chatMessages;
}
