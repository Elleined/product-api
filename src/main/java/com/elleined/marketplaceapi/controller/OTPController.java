//package com.elleined.marketplaceapi.controller;
//
//import com.elleined.marketplaceapi.dto.APIResponse;
//import com.elleined.marketplaceapi.dto.email.OTPMessage;
//import com.elleined.marketplaceapi.service.otp.OTPService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/otp")
//public class OTPController {
//    private final OTPService otpService;
//
//    @PostMapping("/authenticateOTP")
//    public APIResponse authenticateOTP(@RequestParam("userInputOTP") int userInputOTP) {
//        otpService.authenticateOTP(userInputOTP);
//        return new APIResponse(HttpStatus.ACCEPTED, "User OTP authenticated! User may now proceed in changing you password :)");
//    }
//
//    // Use this method also to resendOTP
//    @PostMapping("/sendOTP")
//    public OTPMessage sendOTP(@RequestParam("email") String email) {
//        return otpService.sendOTP(email);
//    }
//}
