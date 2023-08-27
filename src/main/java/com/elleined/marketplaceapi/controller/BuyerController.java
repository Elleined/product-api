package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.mapper.ItemMapper;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.service.user.buyer.BuyerService;
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

    private final BuyerService buyerService;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @PostMapping("/orderProduct")
    public OrderItemDTO orderProduct(@PathVariable("currentUserId") int buyerId,
                                     @Valid @RequestBody OrderItemDTO orderItemDTO) {

        User buyer = userService.getById(buyerId);
        OrderItem orderItem = buyerService.orderProduct(buyer, orderItemDTO);
        return itemMapper.toOrderItemDTO(orderItem);
    }

    @GetMapping("/getAllOrderedProductsByStatus")
    public List<OrderItemDTO> getAllOrderedProductsByStatus(@PathVariable("currentUserId") int buyerId,
                                                            @RequestParam("orderItemStatus") String orderItemStatus) {

        User buyer = userService.getById(buyerId);
        List<OrderItem> orderItems = buyerService.getAllOrderedProductsByStatus(buyer, OrderItem.OrderItemStatus.valueOf(orderItemStatus));

        return orderItems.stream()
                .map(itemMapper::toOrderItemDTO)
                .toList();
    }

    @DeleteMapping("/cancelOrderItem/{orderItemId}")
    public ResponseEntity<OrderItemDTO> cancelOrderItem(@PathVariable("currentUserId") int buyerId,
                                                        @PathVariable("orderItemId") int orderItemId) {

        User buyer = userService.getById(buyerId);
        OrderItem orderItem = userService.getOrderItemById(orderItemId);
        buyerService.cancelOrderItem(buyer, orderItem);
        return ResponseEntity.noContent().build();
    }
}
