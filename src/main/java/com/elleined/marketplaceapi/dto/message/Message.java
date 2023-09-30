package com.elleined.marketplaceapi.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String message;
    private int senderId;
    private String status;
    private LocalDateTime createdAt;
    private String picture;
}
