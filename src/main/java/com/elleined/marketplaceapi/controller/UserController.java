package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.*;
import com.elleined.marketplaceapi.dto.item.CartItemDTO;
import com.elleined.marketplaceapi.service.MarketplaceService;
import com.elleined.marketplaceapi.service.message.MessageService;
import com.elleined.marketplaceapi.service.user.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final MarketplaceService marketplaceService;
    private final PasswordService passwordService;
    private final MessageService messageService;

    @PostMapping
    public UserDTO save(@Valid @RequestBody UserDTO userDTO) {
        return marketplaceService.saveUser(userDTO);
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable("id") int id) {
        return marketplaceService.getUserById(id);
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

    @PostMapping("/{currentUserId}/getDeliveryAddressById/{deliveryAddressId}")
    public AddressDTO getDeliveryAddressById(@PathVariable("currentUserId") int currentUserId,
                                             @PathVariable("deliveryAddressId") int deliveryAddressId) {
        return marketplaceService.getDeliveryAddressById(currentUserId, deliveryAddressId);
    }

    @GetMapping("/getAllSuffix")
    public List<String> getAllSuffix() {
        return marketplaceService.getAllSuffix();
    }


    @PostMapping("/sendPrivateMessage/{recipientId}")
    public PrivateMessage sendPrivateMessage(@PathVariable("recipientId") int recipientId,
                                             @RequestParam("message") String message) {
        return messageService.sendPrivateMessage(recipientId, message);
    }

    @PostMapping("/sendPublicMessage")
    public Message sendPublicMessage(@RequestParam("message") String message) {
        return messageService.sendPublicMessage(message);
    }

    @PatchMapping("/changePassword/{currentUserId}")
    public APIResponse changePassword(@PathVariable("currentUserId") int currentUserId,
                                      @RequestParam("newPassword") String newPassword) {

        passwordService.changePassword(currentUserId, newPassword);
        return new APIResponse(HttpStatus.OK, "Current user successfully changed his/her password");
    }

    @PatchMapping("/{currentUserId}/setProductStateToSold/{productId}")
    public void setProductStateToSold(@PathVariable("currentUserId") int sellerId,
                                      @PathVariable("productId") int productId) {
        marketplaceService.updateProductStateToSold(sellerId, productId);
    }

    @PatchMapping("/{currentUserId}/buyPremium")
    public void buyPremium(@PathVariable("currentUserId") int currentUserId) {
        marketplaceService.buyPremium(currentUserId);
    }
}
