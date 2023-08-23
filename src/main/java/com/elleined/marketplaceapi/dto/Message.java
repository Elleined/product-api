package com.elleined.marketplaceapi.dto;

import lombok.Data;

@Data
public class Message {
    String senderUsername;
    String senderPicture;
    String body;
    int recipientId;
}
