package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.service.GetAllUtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@CrossOrigin(origins = "*")
public class UtilityController {

    private final GetAllUtilityService getAllUtilityService;

    @GetMapping("/usersCount")
    public int usersCount() {
        return getAllUtilityService.getAllUsersCount();
    }

    @GetMapping("/productsCount")
    public int productsCount() {
        return getAllUtilityService.getAllProductCount();
    }

    @GetMapping("/transactionsCount")
    public int transactionsCount() {
        return getAllUtilityService.getAllUsersTransactionsCount();
    }
}
