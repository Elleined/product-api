package com.elleined.marketplaceapi.model.message;


import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tbl_chat_room")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "chat_room_id",
            nullable = false,
            updatable = false,
            unique = true
    )
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "product_to_settle_id",
            referencedColumnName = "product_id",
            nullable = false,
            updatable = false
    )
    private Product productToSettle;

    // Chat room id reference is in tbl_chat_room_message
    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> chatMessages;
}
