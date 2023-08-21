package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.service.MarketplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{currentUserId}/seller")
@CrossOrigin(origins = "*")
public class SellerController {

    private final MarketplaceService marketplaceService;
    @GetMapping("/getAllProductByState")
    public List<ProductDTO> getAllProductByState(@PathVariable("currentUserId") int currentUserId,
                                                 @RequestParam("state") String state) {
        return marketplaceService.getAllProductByState(currentUserId, state);
    }
}
