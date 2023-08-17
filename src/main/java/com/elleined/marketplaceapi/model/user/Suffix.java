package com.elleined.marketplaceapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_name_suffix")
@NoArgsConstructor
@Setter
@Getter
public class Suffix extends BaseEntity {

    @Builder
    public Suffix(int id, String name) {
        super(id, name);
    }
}
