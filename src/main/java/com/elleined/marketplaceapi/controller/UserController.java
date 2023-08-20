package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.service.MarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final MarketplaceService marketplaceService;

    @PostMapping
    public UserDTO save(@Valid @RequestBody UserDTO userDTO) {
        return marketplaceService.saveUser(userDTO);
    }
}
