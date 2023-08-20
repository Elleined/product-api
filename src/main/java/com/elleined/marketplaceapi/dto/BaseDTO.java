package com.elleined.marketplaceapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseDTO {
    private int id;

    @NotBlank(message = "Name cannot be blank, null, or empty!")
    private String name;
}
