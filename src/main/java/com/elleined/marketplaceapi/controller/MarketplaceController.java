package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.service.MarketplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@CrossOrigin(origins = "*")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    @GetMapping("/usersCount")
    public int usersCount() {
        return marketplaceService.usersCount();
    }

    @GetMapping("/productsCount")
    public int productsCount() {
        return marketplaceService.productsCount();
    }

    @GetMapping("/transactionsCount")
    public int transactionsCount() {
        return marketplaceService.transactionsCount();
    }
}
