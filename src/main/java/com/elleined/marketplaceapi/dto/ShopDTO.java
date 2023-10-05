package com.elleined.marketplaceapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDTO {

    @NotBlank(message = "Please provide your shop name")
    private String shopName;

    @NotBlank(message = "Please provide short description about your shop")
    private String description;

    private String picture;
    
    private String validId;
}