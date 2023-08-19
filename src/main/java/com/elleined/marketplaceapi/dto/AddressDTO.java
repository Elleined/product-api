package com.elleined.marketplaceapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDTO {

    private int id;
    @NotBlank(message = "details cannot be null, empty, or blank")
    private String details;
    private String provinceName;
    private String cityName;
    private String baranggayName;
}
