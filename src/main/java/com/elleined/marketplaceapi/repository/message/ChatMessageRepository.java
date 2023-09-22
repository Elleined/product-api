package com.elleined.marketplaceapi.repository.message;

import com.elleined.marketplaceapi.model.message.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
}