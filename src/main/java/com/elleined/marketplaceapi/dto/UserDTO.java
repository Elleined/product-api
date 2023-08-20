package com.elleined.marketplaceapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "First name cannot be blank, null, or empty")
    private String firstName;
    private String middleName;

    @NotBlank(message = "Last name cannot be blank, null, or empty")
    private String lastName;
    private LocalDateTime registrationDate;

    @NotBlank(message = "Gender cannot be blank, null, or empty")
    private String gender;

    @NotNull(message = "Birthday cannot be blank, null, or empty")
    private LocalDateTime birthDate;

    @NotNull(message = "Mobile number cannot be blank, null, or empty")
    private String mobileNumber;

    @NotBlank(message = "Email cannot be blank, null, or empty")
    private String email;
    private String suffix;

    private String validId;
    private String status;
}
