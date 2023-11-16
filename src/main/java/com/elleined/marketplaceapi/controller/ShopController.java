package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.mapper.ShopMapper;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.service.user.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{currentUserId}/shop")
public class ShopController {

    private final UserService userService;
    private final ShopMapper shopMapper;

    private final VerificationService verificationService;

    @PostMapping
    public ShopDTO registerShop(@PathVariable("currentUserId") int currentUserId,
                                @RequestParam("shopName") String shopName,
                                @RequestParam("shopDescription") String shopDescription,
                                @RequestPart("validId") MultipartFile validId,
                                @RequestPart("shopPicture") MultipartFile shopPicture) throws IOException {

        User currentUser = userService.getById(currentUserId);
        verificationService.sendShopRegistration(currentUser, shopName, shopDescription, shopPicture, validId);
        return ShopDTO.builder()
                .shopName(shopName)
                .description(shopDescription)
                .picture(shopPicture.getOriginalFilename())
                .build();
    }

    @GetMapping
    public ShopDTO getShop(@PathVariable("currentUserId") int currentUserId) {
        User seller = userService.getById(currentUserId);
        return shopMapper.toDTO(seller.getShop());
    }
}
