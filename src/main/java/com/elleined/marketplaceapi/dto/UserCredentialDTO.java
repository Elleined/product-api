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
public class UserCredentialDTO {

    @NotBlank(message = "Email cannot be blank, null, or empty")
    private String email;

    @NotBlank(message = "Password cannot be blank, null, or empty")
    private String password;

    @NotBlank(message = "Confirm password cannot be blank, null, or empty")
    private String confirmPassword;
}
