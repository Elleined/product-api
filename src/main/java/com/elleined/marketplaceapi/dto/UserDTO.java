package com.elleined.marketplaceapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
    private String referralCode;
    private String validId;
    private String status;

    @JsonIgnore // Optional if invited
    private String invitationReferralCode;

    @Valid
    @NotNull(message = "User details cannot be null")
    private UserDetailsDTO userDetailsDTO;

    @Valid
    @NotNull(message = "Address cannot be null")
    private AddressDTO addressDTO;

    @Valid
    @NotNull(message = "Address cannot be null")
    private UserCredentialDTO userCredentialDTO;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDetailsDTO {
        @NotBlank(message = "First name cannot be blank, null, or empty")
        private String firstName;
        private String middleName;

        @NotBlank(message = "Last name cannot be blank, null, or empty")
        private String lastName;

        private LocalDateTime registrationDate;

        @NotBlank(message = "Gender cannot be blank, null, or empty")
        private String gender;

        @NotNull(message = "Birthday cannot be blank, null, or empty")
        @Past(message = "Birth Date should be in the past")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime birthDate;

        @NotBlank(message = "Mobile number cannot be blank, null, or empty")
        private String mobileNumber;

        @NotBlank(message = "Mobile number cannot be blank, null, or empty")
        private String picture;

        private String suffix;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCredentialDTO {

        @NotBlank(message = "Email cannot be blank, null, or empty")
        @Email(message = "Enter a valid email")
        private String email;

        @JsonIgnore
        private String password;

        @JsonIgnore
        private String confirmPassword;
    }
}
