package com.elleined.marketplaceapi.controller.moderator;


import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moderators")
public class ModeratorController {
    private final ModeratorService moderatorService;

    @PostMapping("/login")
    public ModeratorDTO login(@Valid @RequestBody CredentialDTO moderatorCredentialDTO) {
        return moderatorService.login(moderatorCredentialDTO);
    }
}
