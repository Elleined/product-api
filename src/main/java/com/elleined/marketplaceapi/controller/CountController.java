package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.model.user.UserDetails;
import com.elleined.marketplaceapi.service.count.CountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CountController {

    private final CountService countService;

    @GetMapping("/users-count")
    public int usersCount() {
        return countService.getAllUsersCount();
    }

    @GetMapping("/products-count")
    public int productsCount() {
        return countService.getAllProductCount();
    }

    @GetMapping("/transactions-count")
    public int transactionsCount() {
        return countService.getAllUsersTransactionsCount();
    }

    @GetMapping("/genders")
    public List<String> getAllGender() {
        return Arrays.stream(UserDetails.Gender.values())
                .map(Enum::name)
                .sorted()
                .toList();
    }

    @GetMapping("/suffixes")
    public List<String> getAllSuffix() {
        return Arrays.stream(UserDetails.Suffix.values())
                .map(Enum::name)
                .sorted()
                .toList();
    }
}
