package com.elleined.marketplaceapi.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class RetailProductDTO extends ProductDTO {

    @Positive(message = "Price per unit cannot be negative or less than 0")
    private double pricePerUnit;
    @Positive(message = "Quantity per unit cannot be negative or less than 0")
    private int quantityPerUnit;

    @Builder(builderMethodName = "retailProductDTOBuilder")
    public RetailProductDTO(int id, @NotBlank(message = "Crop name cannot be null, blank, or empty") String cropName, @Positive(message = "Unit id cannot be negative or less than 0") int unitId, String unitName, @NotBlank(message = "Description cannot be null, blank, or empty") String description, String picture, String state, int sellerId, String sellerName, @Positive(message = "Available quantity cannot be negative or less than 0") int availableQuantity, @NotNull(message = "Harvest date cannot null") @PastOrPresent(message = "Cannot sell an product that are not have been harvested yet") LocalDate harvestDate, @NotNull(message = "Expiration date cannot null") LocalDate expirationDate, LocalDate listingDate, String shopName, double totalPrice, double pricePerUnit, int quantityPerUnit) {
        super(id, cropName, unitId, unitName, description, picture, state, sellerId, sellerName, availableQuantity, harvestDate, expirationDate, listingDate, shopName, totalPrice);
        this.pricePerUnit = pricePerUnit;
        this.quantityPerUnit = quantityPerUnit;
    }
}
