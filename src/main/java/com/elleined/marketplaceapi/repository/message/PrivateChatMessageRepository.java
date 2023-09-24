package com.elleined.marketplaceapi.repository.message;

import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateChatMessageRepository extends JpaRepository<PrivateChatMessage, Integer> {
}