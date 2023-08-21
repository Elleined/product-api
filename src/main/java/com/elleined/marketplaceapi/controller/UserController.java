package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.AddressDTO;
import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.service.MarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable("id") int id) {
        return marketplaceService.getUserById(id);
    }

    @PutMapping("/{currentUserId}")
    public UserDTO update(@PathVariable("currentUserId") int currentUserId,
                          @Valid @RequestBody UserDTO userDTO) {
        return marketplaceService.updateUser(currentUserId, userDTO);
    }

    @PatchMapping("/{currentUserId}/resendValidId")
    public UserDTO resendValidId(@PathVariable("currentUserId") int currentUserId,
                                 @RequestParam("newValidId") String newValidId) {
        return marketplaceService.resendValidId(currentUserId, newValidId);
    }

    @PostMapping("/login")
    public UserDTO login(@Valid @RequestBody UserDTO.UserCredentialDTO userCredentialDTO) {
        return marketplaceService.login(userCredentialDTO);
    }


    @PostMapping("/{currentUserId}/registerShop")
    public ShopDTO registerShop(@PathVariable("currentUserId") int currentUserId,
                                @Valid @RequestBody ShopDTO shopDTO) {

        return marketplaceService.sendShopRegistration(currentUserId, shopDTO);
    }


    @GetMapping("/{currentUserId}/getAllDeliveryAddress")
    public List<AddressDTO> getAllDeliveryAddress(@PathVariable("currentUserId") int currentUserId) {
        return marketplaceService.getAllDeliveryAddress(currentUserId);
    }

    @PostMapping("/{currentUserId}/saveDeliveryAddress")
    public AddressDTO saveDeliveryAddress(@PathVariable("currentUserId") int currentUserId,
                                          @Valid @RequestBody AddressDTO addressDTO) {
        return marketplaceService.saveDeliveryAddress(currentUserId, addressDTO);
    }
}
