package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.GetAllUtilityService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/{currentUserId}/products")
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    private final UserService userService;

    private final GetAllUtilityService getAllUtilityService;
    @GetMapping
    public List<ProductDTO> getAllExcept(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        return productService.getAllExcept(currentUser).stream()
                .map(productMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductDTO getById(@PathVariable("id") int id) {
        Product product = productService.getById(id);
        return productMapper.toDTO(product);
    }

    @GetMapping("/getAllCrops")
    public List<String> getAllCrops() {
        return getAllUtilityService.getAllCrops();
    }

    @GetMapping("/getAllUnits")
    public List<String> getAllUnit() {
        return getAllUtilityService.getAllUnit();
    }

}
