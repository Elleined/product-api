package com.elleined.marketplaceapi.service.email;

import com.elleined.marketplaceapi.dto.email.EmailMessage;
import com.elleined.marketplaceapi.dto.email.OTPMessage;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "email-service", url = "/emailService/api/v1")
interface EmailClient {

    @PostMapping("/sendSimpleMail")
    EmailMessage sendSimpleMail(@Valid @RequestBody EmailMessage emailMessage);

    @PostMapping("/sendOTPMail")
    OTPMessage sendOTPMail(@Valid @RequestBody OTPMessage otpMessage);
}
