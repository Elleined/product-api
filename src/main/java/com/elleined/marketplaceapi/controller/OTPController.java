package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.APIResponse;
import com.elleined.marketplaceapi.service.otp.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/otp")
@CrossOrigin(origins = "*")
public class OTPController {
    private final OTPService otpService;

    @PostMapping("/authenticateOTP")
    public APIResponse authenticateOTP(@RequestParam("userInputOTP") int userInputOTP) {
        otpService.authenticateOTP(userInputOTP);
        return new APIResponse(HttpStatus.ACCEPTED, "User OTP authenticated! User may now proceed in changing you password :)");
    }

    // Use this method also to resendOTP
    @PostMapping("/sendOTP")
    public void sendOTP(@RequestParam("email") String email) {
        otpService.sendOTP(email);
    }
}
