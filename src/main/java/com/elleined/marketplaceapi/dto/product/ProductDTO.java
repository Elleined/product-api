package com.elleined.marketplaceapi.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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

    @Positive(message = "Unit id cannot be negative or less than 0")
    private int unitId;

    private String unitName;

    @NotBlank(message = "Description cannot be null, blank, or empty")
    private String description;

    private String picture;

    private String state;

    private int sellerId;
    private String sellerName;

    private String saleStatus;

    @Positive(message = "Available quantity cannot be negative or less than 0")
    private int availableQuantity;

    @NotNull(message = "Harvest date cannot null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Cannot sell an product that are not have been harvested yet")
    private LocalDate harvestDate;

    private LocalDate listingDate;

    private String shopName;

    private double totalPrice;
}
