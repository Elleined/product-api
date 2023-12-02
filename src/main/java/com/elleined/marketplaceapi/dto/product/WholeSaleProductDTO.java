package com.elleined.marketplaceapi.dto.product;

import com.elleined.marketplaceapi.dto.product.sale.request.SaleWholeSaleRequest;
import com.elleined.marketplaceapi.dto.product.sale.response.SaleWholeSaleResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class WholeSaleProductDTO extends ProductDTO {

    @JsonProperty("salePayload")
    private SaleWholeSaleResponse saleWholeSaleResponse;

    @Builder(builderMethodName = "wholeSaleProductDTOBuilder")
    public WholeSaleProductDTO(int id, @NotBlank(message = "Crop name cannot be null, blank, or empty") String cropName, @Positive(message = "Unit id cannot be negative or less than 0") int unitId, String unitName, @NotBlank(message = "Description cannot be null, blank, or empty") String description, String picture, String state, int sellerId, String sellerName, @Positive(message = "Available quantity cannot be negative or less than 0") int availableQuantity, @NotNull(message = "Harvest date cannot null") @PastOrPresent(message = "Cannot sell an product that are not have been harvested yet") LocalDate harvestDate, LocalDate listingDate, String shopName, double totalPrice, SaleWholeSaleResponse saleWholeSaleResponse) {
        super(id, cropName, unitId, unitName, description, picture, state, sellerId, sellerName, availableQuantity, harvestDate, listingDate, shopName, totalPrice);
        this.saleWholeSaleResponse = saleWholeSaleResponse;
    }
}