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

    @GetMapping("/state")
    public List<ProductDTO> getAllProductByState(@PathVariable("currentUserId") int currentUserId,
                                                 @RequestParam("state") String state) {
        return marketplaceService.getAllProductByState(currentUserId, state);
    }

    @GetMapping("/{id}")
    public ProductDTO getById(@PathVariable("currentUserId") int currentUserId,
                              @PathVariable("id") int id) {
        return marketplaceService.getProductById(currentUserId, id);
    }

    @PostMapping
    public ProductDTO save(@PathVariable("currentUserId") int currentUserId,
                           @Valid @RequestBody ProductDTO productDTO) {

        return marketplaceService.saveByDTO(currentUserId, productDTO);
    }

    @PutMapping("/{id}")
    public ProductDTO update(@PathVariable("currentUserId") int currentUserId,
                             @PathVariable("id") int id,
                             @Valid @RequestBody ProductDTO productDTO) {

        return marketplaceService.updateProduct(currentUserId, id, productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDTO> deleteById(@PathVariable("currentUserId") int currentUserId,
                                     @PathVariable("id") int id) {

        marketplaceService.deleteProduct(currentUserId, id);
        return ResponseEntity.noContent().build();
    }
}
