package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.item.CartItemDTO;
import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.service.MarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{currentUserId}/cartItems")
@CrossOrigin(origins = "*")
public class CartItemController {

    private final MarketplaceService marketplaceService;
    @GetMapping
    public List<CartItemDTO> getAll(@PathVariable("currentUserId") int currentUserId) {
        return marketplaceService.getAllCartItems(currentUserId);
    }

    @PostMapping
    public CartItemDTO save(@PathVariable("currentUserId") int currentUserId,
                            @Valid @RequestBody CartItemDTO cartItemDTO) {
        return marketplaceService.saveCartItem(currentUserId, cartItemDTO);
    }

    @DeleteMapping("/{cartItemId}")
    public void delete(@PathVariable("currentUserId") int currentUserId,
                       @PathVariable("cartItemId") int cartItemId) throws ResourceNotFoundException {
        marketplaceService.deleteCartItem(currentUserId, cartItemId);
    }


    @PostMapping("/orderCartItem/{cartItemId}")
    public OrderItemDTO moveToOrderItem(@PathVariable("currentUserId") int currentUserId,
                                        @PathVariable("cartItemId") int cartItemId) {
        return marketplaceService.moveToOrderItem(currentUserId, cartItemId);
    }


    @PostMapping("/orderAllCartItem")
    public List<OrderItemDTO> moveAllToOrderItem(@PathVariable("currentUserId") int currentUserId,
                                                 @RequestBody List<Integer> cartItemIds) {
        return marketplaceService.moveAllToOrderItem(currentUserId, cartItemIds);
    }
}
