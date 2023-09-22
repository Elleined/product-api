package com.elleined.marketplaceapi.repository.message;

import com.elleined.marketplaceapi.model.message.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
}