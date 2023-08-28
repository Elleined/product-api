package com.elleined.marketplaceapi.populator;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Set;

@Component
@Qualifier("userPopulator")
@Transactional
public class UserPopulator extends Populator {

    private final UserService userService;

    public UserPopulator(ObjectMapper objectMapper,
                         UserService userService) {

        super(objectMapper);
        this.userService = userService;
    }

    @Override
    public void populate(String jsonFile) throws IOException {
        var resource = new ClassPathResource(jsonFile);
        var type = objectMapper.getTypeFactory().constructCollectionType(Set.class, UserDTO.class);

        Set<UserDTO> users = objectMapper.readValue(resource.getFile(), type);
        users.forEach(userService::saveByDTO);
    }
}
