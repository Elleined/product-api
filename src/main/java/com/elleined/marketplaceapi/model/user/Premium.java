package com.elleined.marketplaceapi.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_user_premium")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Premium {

    @Id
    @Column(
            name = "owner_id",
            nullable = false,
            updatable = false
    )
    private int id;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    // This is the primary key of this shop table
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(
            name = "owner_id",
            referencedColumnName = "user_id"
    )
    private User user;
}
