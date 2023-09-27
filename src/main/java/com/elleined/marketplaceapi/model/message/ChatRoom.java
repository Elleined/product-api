package com.elleined.marketplaceapi.model.message;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_agreement_status", nullable = false)
    private Status isSenderAcceptedAgreement;

    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_agreement_status", nullable = false)
    private Status isReceiverAcceptedAgreement;

    public enum Status { ACCEPTED, NOT_ACCEPTED }
}
