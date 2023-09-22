package com.elleined.marketplaceapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PrivateMessage extends Message {
    private int recipientId;


    @Builder
    public PrivateMessage(String message, int senderId, int recipientId) {
        super(message, senderId);
        this.recipientId = recipientId;
    }
}
