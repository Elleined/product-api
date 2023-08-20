package com.elleined.marketplaceapi.model.user;

import com.elleined.marketplaceapi.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(
        name = "tbl_user_name_suffix",
        indexes = @Index(name = "name_idx", columnList = "name")
)
@NoArgsConstructor
@Setter
@Getter
public class Suffix extends BaseEntity {

    @OneToMany(mappedBy = "suffix")
    private List<User> user;

    @Builder

    public Suffix(int id, String name, List<User> user) {
        super(id, name);
        this.user = user;
    }
}
