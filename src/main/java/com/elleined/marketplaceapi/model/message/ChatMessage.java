package com.elleined.marketplaceapi.model.message;


import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_chat_room_message")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "chat_room_message_id",
            nullable = false,
            updatable = false,
            unique = true
    )
    private int id;

    @Column(name = "message", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String message;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "chat_room_id",
            referencedColumnName = "chat_room_id",
            nullable = false,
            updatable = false
    )
    private ChatRoom chatRoom;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "sender_id",
            referencedColumnName = "user_id",
            nullable = false,
            updatable = false
    )
    private User sender;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {ACTIVE, INACTIVE}
}
