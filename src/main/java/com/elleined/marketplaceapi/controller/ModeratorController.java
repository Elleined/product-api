package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import com.elleined.marketplaceapi.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{moderatorId}/moderator")
@CrossOrigin(origins = "*")
public class ModeratorController {
    private final ModeratorService moderatorService;
    private final ProductService productService;

    @GetMapping("/getAllUnverifiedUser")
    public List<UserDTO> getAllUnverifiedUser() {
        return moderatorService.getAllUnverifiedUser();
    }

    @GetMapping("/getAllPendingProduct")
    public List<ProductDTO> getAllPendingProduct() {
        return moderatorService.getAllPendingProduct();
    }


    @PatchMapping("/verifyUser/{userToBeVerifiedId}")
    public void verifyUser(@PathVariable("userToBeVerifiedId") int userToBeVerifiedId) {
        moderatorService.verifyUser(userToBeVerifiedId);
    }

    @PatchMapping("/verifyAllUser")
    public void verifyAllUser(@RequestBody List<Integer> userToBeVerifiedIds) {
        moderatorService.verifyAllUser(userToBeVerifiedIds);
    }


    @PatchMapping("/listProduct/{productId}")
    public void listProduct(@PathVariable("productId") int productId) throws ResourceNotFoundException {
        Product product = productService.getById(productId);
        if (product.isDeleted()) throw new ResourceNotFoundException("Product with id of " + productId + " are already been deleted or does not exists!");
        moderatorService.listProduct(productId);
    }

    @PatchMapping("/listAllProduct")
    public void listAllProduct(@RequestParam List<Integer> productIds) {
        moderatorService.listAllProduct(productIds);
    }
}
