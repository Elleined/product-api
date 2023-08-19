package com.elleined.marketplaceapi.model.user;

import com.elleined.marketplaceapi.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
