package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.service.MarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/updateOrderItemStatus/{orderItemId}")
    public void updateOrderItemStatus(@PathVariable("currentUserId") int currentUserId,
                                      @PathVariable("orderItemId") int orderItemId,
                                      @RequestParam("newOrderItemStatus") String newOrderItemStatus,
                                      @RequestParam("messageToBuyer") String messageToBuyer) {
        marketplaceService.updateOrderItemStatus(currentUserId, orderItemId, newOrderItemStatus, messageToBuyer);
    }

    @GetMapping("/getAllSellerProductOrderByStatus")
    public List<OrderItemDTO> getAllSellerProductOrderByStatus(@PathVariable("currentUserId") int sellerId,
                                                               @RequestParam("orderItemStatus") String orderItemStatus) {
        return marketplaceService.getAllSellerProductOrderByStatus(sellerId, orderItemStatus);
    }

    @PostMapping("/saveProduct")
    public ProductDTO saveProduct(@PathVariable("currentUserId") int currentUserId,
                                  @Valid @RequestBody ProductDTO productDTO) {

        return marketplaceService.saveByDTO(currentUserId, productDTO);
    }

    @PutMapping("/updateProduct/{id}")
    public ProductDTO update(@PathVariable("currentUserId") int currentUserId,
                             @PathVariable("id") int id,
                             @Valid @RequestBody ProductDTO productDTO) {

        return marketplaceService.updateProduct(currentUserId, id, productDTO);
    }

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<ProductDTO> deleteById(@PathVariable("currentUserId") int currentUserId,
                                                 @PathVariable("id") int id) {

        marketplaceService.deleteProduct(currentUserId, id);
        return ResponseEntity.noContent().build();
    }
}
