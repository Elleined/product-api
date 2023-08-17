package com.elleined.marketplaceapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    private int id;
    private String uuid;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String gender;
    private String suffix;
}
