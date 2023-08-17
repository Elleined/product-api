package com.elleined.marketplaceapi.mapper;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class UserMapper {


    @Mappings({

    })
    public abstract User toEntity(UserDTO userDTO);

    @Mappings({

    })
    public abstract UserDTO toDTO(User user);


    @Mappings({

    })
    public abstract void toUpdate(UserDTO userDTO, @MappingTarget User user);
}
