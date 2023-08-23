package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
// Use to track the logged in user to be use in web socket user handshake handler
public class PrincipalService {
    private User principal;
}
