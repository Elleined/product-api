package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.*;
import com.elleined.marketplaceapi.dto.address.DeliveryAddressDTO;
import com.elleined.marketplaceapi.mapper.AddressMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.GetAllUtilityService;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.email.EmailService;
import com.elleined.marketplaceapi.service.message.MessageService;
import com.elleined.marketplaceapi.service.user.PasswordService;
import com.elleined.marketplaceapi.service.user.PremiumService;
import com.elleined.marketplaceapi.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final EmailService emailService;

    private final PasswordService passwordService;
    private final MessageService messageService;

    private final PremiumService premiumService;
    private final UserMapper userMapper;

    private final AddressService addressService;
    private final AddressMapper addressMapper;

    private final GetAllUtilityService getAllUtilityService;

    @PostMapping
    public UserDTO save(@Valid @RequestBody UserDTO userDTO) {
        User registeringUser = userService.saveByDTO(userDTO);
        emailService.sendWelcomeEmail(registeringUser);
        return userMapper.toDTO(registeringUser);
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable("id") int id) {
        User user = userService.getById(id);
        return userMapper.toDTO(user);
    }

    @GetMapping("/getAllGender")
    public List<String> getAllGender() {
        return getAllUtilityService.getAllGender();
    }

    @GetMapping("/getAllSuffix")
    public List<String> getAllSuffix() {
        return getAllUtilityService.getAllSuffix();
    }

    @PatchMapping("/{currentUserId}/resendValidId")
    public UserDTO resendValidId(@PathVariable("currentUserId") int currentUserId,
                                 @RequestParam("newValidId") String newValidId) {

        User currentUser = userService.getById(currentUserId);
        userService.resendValidId(currentUser, newValidId);
        return userMapper.toDTO(currentUser);
    }

    @PostMapping("/login")
    public UserDTO login(@Valid @RequestBody CredentialDTO userCredentialDTO,
                         HttpSession session) {
        User currentUser = userService.login(userCredentialDTO);
        session.setAttribute("currentUser", currentUser);
        return userMapper.toDTO(currentUser);
    }


    @PostMapping("/{currentUserId}/registerShop")
    public ShopDTO registerShop(@PathVariable("currentUserId") int currentUserId,
                                @Valid @RequestBody ShopDTO shopDTO) {

        User currentUser = userService.getById(currentUserId);
        userService.sendShopRegistration(currentUser, shopDTO);
        return shopDTO;
    }


    @GetMapping("/{currentUserId}/getAllDeliveryAddress")
    public List<DeliveryAddressDTO> getAllDeliveryAddress(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        List<DeliveryAddress> deliveryAddresses = addressService.getAllDeliveryAddress(currentUser);
        return deliveryAddresses.stream()
                .map(addressMapper::toDeliveryAddressDTO)
                .toList();
    }

    @PostMapping("/{currentUserId}/saveDeliveryAddress")
    public DeliveryAddressDTO saveDeliveryAddress(@PathVariable("currentUserId") int currentUserId,
                                          @Valid @RequestBody DeliveryAddressDTO deliveryAddressDTO) {
        User currentUser = userService.getById(currentUserId);
        DeliveryAddress deliveryAddress = addressService.saveDeliveryAddress(currentUser, deliveryAddressDTO);
        return addressMapper.toDeliveryAddressDTO(deliveryAddress);
    }

    @GetMapping("/{currentUserId}/getDeliveryAddressById/{deliveryAddressId}")
    public DeliveryAddressDTO getDeliveryAddressById(@PathVariable("currentUserId") int currentUserId,
                                             @PathVariable("deliveryAddressId") int deliveryAddressId) {

        User currentUser = userService.getById(currentUserId);
        DeliveryAddress deliveryAddress = addressService.getDeliveryAddressById(currentUser, deliveryAddressId);
        return addressMapper.toDeliveryAddressDTO(deliveryAddress);
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

    @PatchMapping("/{currentUserId}/account/changePassword")
    public APIResponse accountChangePassword(@PathVariable("currentUserId") int currentUserId,
                                             @RequestParam("oldPassword") String oldPassword,
                                             @RequestParam("newPassword") String newPassword,
                                             @RequestParam("retypeNewPassword") String retypeNewPassword) {

        User currentUser = userService.getById(currentUserId);
        passwordService.changePassword(currentUser, oldPassword, newPassword, retypeNewPassword);
        return new APIResponse(HttpStatus.OK, "User with id of {} successfully changed his/her password");
    }

    @PatchMapping("/{currentUserId}/forgot/changePassword")
    public APIResponse forgotChangePassword(@RequestParam("email") String email,
                                            @RequestParam("newPassword") String newPassword,
                                            @RequestParam("retypeNewPassword") String retypeNewPassword) {

        passwordService.changePassword(email, newPassword, retypeNewPassword);
        return new APIResponse(HttpStatus.OK, "User with email of {} successfully changed his/her password");
    }

    @PatchMapping("/{currentUserId}/buyPremium")
    public void buyPremium(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        premiumService.upgradeToPremium(currentUser);
    }
}
