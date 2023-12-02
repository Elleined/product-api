package com.elleined.marketplaceapi.dto.product;

import com.elleined.marketplaceapi.dto.product.sale.request.SaleRetailProductRequest;
import com.elleined.marketplaceapi.dto.product.sale.response.SaleRetailProductResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class RetailProductDTO extends ProductDTO {

    @Positive(message = "Price per unit cannot be negative or less than 0")
    private double pricePerUnit;
    @Positive(message = "Quantity per unit cannot be negative or less than 0")
    private int quantityPerUnit;

    @NotNull(message = "Expiration date cannot null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    @JsonProperty("salePayload")
    private SaleRetailProductResponse saleRetailProductResponse;


    @Builder(builderMethodName = "retailProductDTOBuilder")
    public RetailProductDTO(int id, @NotBlank(message = "Crop name cannot be null, blank, or empty") String cropName, @Positive(message = "Unit id cannot be negative or less than 0") int unitId, String unitName, @NotBlank(message = "Description cannot be null, blank, or empty") String description, String picture, String state, int sellerId, String sellerName, @Positive(message = "Available quantity cannot be negative or less than 0") int availableQuantity, @NotNull(message = "Harvest date cannot null") @PastOrPresent(message = "Cannot sell an product that are not have been harvested yet") LocalDate harvestDate, LocalDate listingDate, String shopName, double totalPrice, double pricePerUnit, int quantityPerUnit, LocalDate expirationDate, SaleRetailProductResponse saleRetailProductResponse) {
        super(id, cropName, unitId, unitName, description, picture, state, sellerId, sellerName, availableQuantity, harvestDate, listingDate, shopName, totalPrice);
        this.pricePerUnit = pricePerUnit;
        this.quantityPerUnit = quantityPerUnit;
        this.expirationDate = expirationDate;
        this.saleRetailProductResponse = saleRetailProductResponse;
    }
}
