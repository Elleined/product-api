package com.elleined.marketplaceapi.client;

import com.elleined.marketplaceapi.dto.email.EmailMessage;
import com.elleined.marketplaceapi.dto.email.OTPMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// @FeignClient(name = "EMAIL-SERVICE", url = "http://localhost:8091/api/v1/email-service-api" )
public interface EmailClient {

//    @PostMapping("/sendSimpleMail")
//    EmailMessage sendSimpleMail(@RequestBody EmailMessage emailMessage);
//
//    @PostMapping("/sendOTPMail")
//    OTPMessage sendOTPMail(@RequestBody OTPMessage otpMessage);
}
