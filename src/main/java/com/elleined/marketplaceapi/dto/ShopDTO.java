package com.elleined.marketplaceapi.dto;

import jakarta.validation.constraints.NotEmpty;
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

    @NotBlank(message = "Please provide shop picture")
    private String picture;

    @NotBlank(message = "Please provide valid id for verification")
    private String validId;
}