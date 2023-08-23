package com.elleined.marketplaceapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private int id;

    @NotBlank(message = "Crop name cannot be null, blank, or empty")
    private String cropName;

    @NotBlank(message = "Unit name cannot be null, blank, or empty")
    private String unitName;

    @NotBlank(message = "Description cannot be null, blank, or empty")
    private String description;

    @NotBlank(message = "Picture cannot be null, blank, or empty")
    private String picture;

    @NotBlank(message = "Keyword cannot be null, blank, or empty")
    private String keyword;
    private String state;
    private String status;

    private int sellerId;
    private String sellerName;

    @Positive(message = "Quantity cannot be negative or less than 0")
    private int availableQuantity;
    @Positive(message = "Price per unit cannot be negative or less than 0")
    private double pricePerUnit;
    @Positive(message = "Quantity per unit cannot be negative or less than 0")
    private int quantityPerUnit;

    @NotNull(message = "Harvest date cannot null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @PastOrPresent(message = "Cannot sell an item that are not have been harvested yet")
    private LocalDateTime harvestDate;
    private LocalDateTime listingDate;

    private String shopName;
}
