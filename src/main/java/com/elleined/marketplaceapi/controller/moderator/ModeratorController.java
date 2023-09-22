package com.elleined.marketplaceapi.controller.moderator;


import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import com.elleined.marketplaceapi.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moderators")
public class ModeratorController {
    private final ModeratorService moderatorService;

    private final UserService userService;
    private final UserMapper userMapper;


    @PostMapping("/login")
    public ModeratorDTO login(@Valid @RequestBody CredentialDTO moderatorCredentialDTO) {
        return moderatorService.login(moderatorCredentialDTO);
    }

    @GetMapping("/sellers")
    public Set<UserDTO> getAllSeller() {
        return userService.getAllSeller().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toSet());
    }

    @GetMapping("/sellers/search")
    public Set<UserDTO> searchByName(@RequestParam("username") String username) {
        return userService.searchAllSellerByName(username).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toSet());
    }
}
