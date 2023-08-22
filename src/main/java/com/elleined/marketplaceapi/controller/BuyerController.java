package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.service.MarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{currentUserId}/buyer")
@CrossOrigin(origins = "*")
public class BuyerController {

    private final MarketplaceService marketplaceService;
    @PostMapping("/orderProduct")
    public OrderItemDTO orderProduct(@PathVariable("currentUserId") int currentUserId,
                                     @Valid @RequestBody OrderItemDTO orderItemDTO) {
        return marketplaceService.orderProduct(currentUserId, orderItemDTO);
    }

    @GetMapping("/getAllOrderedProductsByStatus")
    public List<OrderItemDTO> getAllOrderedProductsByStatus(@PathVariable("currentUserId") int currentUserId,
                                                            @RequestParam("orderItemStatus") String orderItemStatus) {

        return marketplaceService.getAllOrderedProductsByStatus(currentUserId, orderItemStatus);
    }

    @DeleteMapping("/cancelOrderItem/{orderItemId}")
    public ResponseEntity<OrderItemDTO> cancelOrderItem(@PathVariable("currentUserId") int currentUserId,
                                                        @PathVariable("orderItemId") int orderItemId) {

        marketplaceService.cancelOrderItem(currentUserId, orderItemId);
        return ResponseEntity.noContent().build();
    }
}
