package com.elleined.marketplaceapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialDTO {

    @NotBlank(message = "Email cannot be blank, null, or empty")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Password cannot be blank, null, or empty")
    private String password;

    private String confirmPassword;
}
