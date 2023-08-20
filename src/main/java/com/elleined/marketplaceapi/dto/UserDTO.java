package com.elleined.marketplaceapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime birthDate;

    @NotBlank(message = "Mobile number cannot be blank, null, or empty")
    private String mobileNumber;

    private String suffix;

    private String validId;
    private String status;

    @Valid
    @NotNull(message = "Address cannot be null")
    private AddressDTO addressDTO;

    @Valid
    @NotNull(message = "Address cannot be null")
    private UserCredentialDTO userCredentialDTO;
}
