package com.elleined.marketplaceapi.model.user;

import com.elleined.marketplaceapi.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tbl_user_name_suffix",
        indexes = @Index(name = "name_idx", columnList = "name")
)
@NoArgsConstructor
@Setter
@Getter
public class Suffix extends BaseEntity {

    @OneToOne(mappedBy = "suffix")
    private User user;

    @Builder
    public Suffix(int id, String name, User user) {
        super(id, name);
        this.user = user;
    }
}
