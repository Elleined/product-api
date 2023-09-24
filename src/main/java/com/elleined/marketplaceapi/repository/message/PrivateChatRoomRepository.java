package com.elleined.marketplaceapi.repository.message;

import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateChatRoomRepository extends JpaRepository<PrivateChatRoom, Integer> {
}