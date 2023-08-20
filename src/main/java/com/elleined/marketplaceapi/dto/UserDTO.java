package com.elleined.marketplaceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private int id;
    private String uuid;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String gender;
    private String suffix;
    private String status;
}
