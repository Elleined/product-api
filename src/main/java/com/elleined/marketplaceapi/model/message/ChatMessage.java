package com.elleined.marketplaceapi.model.message;

import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "tbl_chat_room_message")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ChatMessage {

    @Id
    @GeneratedValue(
            strategy = GenerationType.TABLE,
            generator = "chatRoomMessageAutoIncrement"
    )
    @SequenceGenerator(
            allocationSize = 1,
            name = "chatRoomMessageAutoIncrement",
            sequenceName = "chatRoomMessageAutoIncrement"
    )
    @Column(
            name = "chat_room_message_id",
            nullable = false,
            updatable = false,
            unique = true
    )
    private int id;

    @Column(
            name = "message",
            nullable = false,
            updatable = false,
            columnDefinition = "MEDIUMTEXT"
    )
    private String message;

    @Column(
            name = "picture",
            updatable = false
    )
    private String picture;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "sender_id",
            referencedColumnName = "user_id",
            nullable = false,
            updatable = false
    )
    private User sender;

    @Column(
            name = "created_at",
            updatable = false,
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {ACTIVE, INACTIVE}

    public boolean isNotDeleted() {
        return this.status == Status.ACTIVE;
    }
}
