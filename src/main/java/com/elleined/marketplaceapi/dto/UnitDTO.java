package com.elleined.marketplaceapi.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class UnitDTO {
    int id;
    String name;
}
