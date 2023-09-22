package com.elleined.marketplaceapi.dto.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PrivateChatMessageDTO extends Message {
    private int id;
    private int privateRoomId;
    private int productToSettleId;
}
