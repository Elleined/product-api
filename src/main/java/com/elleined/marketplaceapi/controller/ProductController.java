package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.service.MarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/{currentUserId}/products")
@CrossOrigin(origins = "*")
public class ProductController {
    private final MarketplaceService marketplaceService;

    @GetMapping
    public List<ProductDTO> getAllExcept(@PathVariable("currentUserId") int currentUserId) {
        return marketplaceService.getAllProductExcept(currentUserId);
    }

    @GetMapping("/{id}")
    public ProductDTO getById(@PathVariable("currentUserId") int currentUserId,
                              @PathVariable("id") int id) {
        return marketplaceService.getProductById(currentUserId, id);
    }

    @GetMapping("/getAllCrops")
    public List<String> getAllCrops() {
        return marketplaceService.getAllCrops();
    }

    @GetMapping("/getAllUnits")
    public List<String> getAllUnit() {
        return marketplaceService.getAllUnit();
    }

}
