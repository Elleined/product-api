package com.elleined.marketplaceapi.dto.item;


import com.elleined.marketplaceapi.model.product.Product;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public abstract class OrderItemDTO extends ItemDTO {

    private String orderItemStatus;

    private String sellerMessage;

    private LocalDateTime updatedAt;
}
