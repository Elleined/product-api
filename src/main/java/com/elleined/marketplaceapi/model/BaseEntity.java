package com.elleined.marketplaceapi.model;

// so basically @MappedSuperclass annotation is use to defined all entities common field even if they are not directly related

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private int id;

    @Column(
            name = "name",
            nullable = false,
            updatable = false
    )
    private String name;
}
