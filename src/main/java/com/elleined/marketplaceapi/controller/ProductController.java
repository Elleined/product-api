//package com.elleined.marketplaceapi.controller;
//
//import com.elleined.marketplaceapi.dto.product.ProductDTO;
//import com.elleined.marketplaceapi.mapper.product.ProductMapper;
//import com.elleined.marketplaceapi.model.product.Product;
//import com.elleined.marketplaceapi.model.user.User;
//import com.elleined.marketplaceapi.service.GetAllUtilityService;
//import com.elleined.marketplaceapi.service.product.ProductService;
//import com.elleined.marketplaceapi.service.user.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/products")
//public class ProductController {
//    private final ProductService productService;
//    private final ProductMapper productMapper;
//
//    private final UserService userService;
//
//    private final GetAllUtilityService getAllUtilityService;
//    @GetMapping("/{currentUserId}/getAllProducts")
//    public List<ProductDTO> getAllExcept(@PathVariable("currentUserId") int currentUserId) {
//        User currentUser = userService.getById(currentUserId);
//        return productService.getAllExcept(currentUser).stream()
//                .map(productMapper::toDTO)
//                .toList();
//    }
//
//    @GetMapping("/{id}")
//    public ProductDTO getById(@PathVariable("id") int id) {
//        Product product = productService.getById(id);
//        return productMapper.toDTO(product);
//    }
//
//    @GetMapping("/getAllCrops")
//    public List<String> getAllCrops() {
//        return getAllUtilityService.getAllCrops();
//    }
//
//    @GetMapping("/getAllUnits")
//    public List<String> getAllUnit() {
//        return getAllUtilityService.getAllUnit();
//    }
//
//    @GetMapping("/{productId}/calculate/order-price")
//    public double calculateOrderPrice(@PathVariable("productId") int productId,
//                                      @RequestParam("userOrderQuantity") int userOrderQuantity) {
//
//        Product product = productService.getById(productId);
//        return productService.calculateOrderPrice(product, userOrderQuantity);
//    }
//
//    @GetMapping("/search-by-crop-name")
//    public List<ProductDTO> searchProductByCropName(@RequestParam("cropName") String cropName) {
//        return productService.searchProductByCropName(cropName).stream()
//                .map(productMapper::toDTO)
//                .toList();
//    }
//}
