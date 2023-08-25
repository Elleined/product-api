package com.elleined.marketplaceapi.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Embeddable
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, updatable = false)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "mobile_number", nullable = false, unique = true)
    private String mobileNumber;

    @Column(name = "picture", nullable = false)
    private String picture;

    // user id reference is in suffix table
    @ManyToOne
    @JoinColumn(
            name = "suffix_id",
            referencedColumnName = "id"
    )
    private Suffix suffix;

    public enum Gender {
        MALE,
        FEMALE,
        LGBTQ_MEMBER,
        PREFER_NOT_TO_SAY
    }
}
