package com.elleined.marketplaceapi.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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

    private int sellerId;
    private String sellerName;

    @Positive(message = "Available quantity cannot be negative or less than 0")
    private int availableQuantity;
    @Positive(message = "Price per unit cannot be negative or less than 0")
    private double pricePerUnit;
    @Positive(message = "Quantity per unit cannot be negative or less than 0")
    private int quantityPerUnit;

    @NotNull(message = "Harvest date cannot null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Cannot sell an product that are not have been harvested yet")
    private LocalDate harvestDate;

    @NotNull(message = "Expiration date cannot null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")

    private LocalDate expirationDate;

    private LocalDate listingDate;

    private String shopName;

    private double totalPrice;
}
