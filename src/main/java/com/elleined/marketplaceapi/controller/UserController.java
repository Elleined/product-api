package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.APIResponse;
import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.email.EmailService;
import com.elleined.marketplaceapi.service.user.PasswordService;
import com.elleined.marketplaceapi.service.user.PremiumService;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.service.user.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final VerificationService verificationService;
    private final UserService userService;

    private final EmailService emailService;

    private final PasswordService passwordService;

    private final PremiumService premiumService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDTO save(@Valid @RequestPart("userDTO") UserDTO userDTO,
                        @RequestPart(value = "profilePicture") MultipartFile profilePicture) throws IOException {
        User registeringUser = userService.saveByDTO(userDTO, profilePicture);

        emailService.sendWelcomeEmail(registeringUser);
        return userMapper.toDTO(registeringUser);
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable("id") int id) {
        User user = userService.getById(id);
        return userMapper.toDTO(user);
    }

    @PatchMapping("/{currentUserId}/resend-valid-id")
    public UserDTO resendValidId(@PathVariable("currentUserId") int currentUserId,
                                 @RequestPart("newValidId") MultipartFile newValidId) throws IOException {

        User currentUser = userService.getById(currentUserId);
        verificationService.resendValidId(currentUser, newValidId);
        return userMapper.toDTO(currentUser);
    }

    @PostMapping("/login")
    public UserDTO login(@Valid @RequestBody CredentialDTO userCredentialDTO) {
        User currentUser = userService.login(userCredentialDTO);
        return userMapper.toDTO(currentUser);
    }

    @PatchMapping("/{currentUserId}/account/change-password")
    public APIResponse accountChangePassword(@PathVariable("currentUserId") int currentUserId,
                                             @RequestParam("oldPassword") String oldPassword,
                                             @RequestParam("newPassword") String newPassword,
                                             @RequestParam("retypeNewPassword") String retypeNewPassword) {

        User currentUser = userService.getById(currentUserId);
        passwordService.changePassword(currentUser, oldPassword, newPassword, retypeNewPassword);
        return new APIResponse(HttpStatus.OK, "User with id of {} successfully changed his/her password");
    }

    @PatchMapping("/forgot/change-password")
    public APIResponse forgotChangePassword(@RequestParam("email") String email,
                                            @RequestParam("newPassword") String newPassword,
                                            @RequestParam("retypeNewPassword") String retypeNewPassword) {

        passwordService.changePassword(email, newPassword, retypeNewPassword);
        return new APIResponse(HttpStatus.OK, "User with email of {} successfully changed his/her password");
    }

    @PatchMapping("/{currentUserId}/buy-premium")
    public void buyPremium(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        premiumService.upgradeToPremium(currentUser);
    }
}
