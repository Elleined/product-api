package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.service.MarketplaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{currentUserId}/buyer")
@CrossOrigin(origins = "*")
public class BuyerController {

    private final MarketplaceService marketplaceService;
    @PostMapping
    public OrderItemDTO orderProduct(@PathVariable("currentUserId") int currentUserId,
                                     @Valid @RequestBody OrderItemDTO orderItemDTO) {
        return marketplaceService.orderProduct(currentUserId, orderItemDTO);
    }

}
