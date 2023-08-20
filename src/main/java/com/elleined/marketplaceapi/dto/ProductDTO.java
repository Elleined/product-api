package com.elleined.marketplaceapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductDTO {
    private int id;

    private int cropId;
    private String cropName;

    private int unitId;
    private String unitName;

    private String description;
    private String picture;
    private String keyword;
    private String state;
    private String status;

    private int sellerId;
    private String sellerName;

    private int availableQuantity;
    private double pricePerUnit;
    private int quantityPerUnit;
    private LocalDateTime harvestDate;
    private LocalDateTime listingDate;
}
