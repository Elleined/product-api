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
public class AddressDTO {

    private int id;

    @NotBlank(message = "Details cannot be null, empty, or blank")
    private String details;

    @NotBlank(message = "Province name cannot be null, empty, or blank")
    private String provinceName;

    @NotBlank(message = "City name cannot be null, empty, or blank")
    private String cityName;

    @NotBlank(message = "Baranggay name cannot be null, empty, or blank")
    private String baranggayName;
}
