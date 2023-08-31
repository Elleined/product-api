package com.elleined.marketplaceapi.dto;

import com.elleined.marketplaceapi.dto.address.AddressDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    private String invitationReferralCode;

    private BigDecimal balance;

    @Valid
    @NotNull(message = "User details cannot be null")
    private UserDetailsDTO userDetailsDTO;

    @Valid
    @NotNull(message = "Address cannot be null")
    private AddressDTO addressDTO;

    @Valid
    @NotNull(message = "Credentials cannot be null")
    private CredentialDTO userCredentialDTO;

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
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDate;


        @NotBlank(message = "Mobile number cannot be blank, null, or empty")
        private String mobileNumber;

        @NotBlank(message = "Picture cannot be blank, null, or empty")
        private String picture;

        @NotBlank(message = "Suffix id cannot be less than zero. If user has no suffix use NONE")
        private String suffix;
    }
}
