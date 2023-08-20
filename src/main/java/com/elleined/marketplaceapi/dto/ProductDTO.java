package com.elleined.marketplaceapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductDTO {
    private int id;

    @Positive(message = "Crop id cannot be negative or less than 0")
    private int cropId;
    private String cropName;

    @Positive(message = "Unit id cannot be negative or less than 0")
    private int unitId;
    private String unitName;

    @NotBlank(message = "Description cannot be null, blank, or empty")
    private String description;

    @NotBlank(message = "Picture cannot be null, blank, or empty")
    private String picture;
    private String keyword;
    private String state;
    private String status;

    @Positive(message = "Seller id cannot be negative or less than 0")
    private int sellerId;
    private String sellerName;

    @Positive(message = "Quantity cannot be negative or less than 0")
    private int availableQuantity;
    @Positive(message = "Price per unit cannot be negative or less than 0")
    private double pricePerUnit;
    @Positive(message = "Quantity per unit cannot be negative or less than 0")
    private int quantityPerUnit;

    @NotNull(message = "Harvest date cannot null")
    private LocalDateTime harvestDate;
    private LocalDateTime listingDate;
}
