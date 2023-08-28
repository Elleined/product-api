package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.mapper.ItemMapper;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.service.user.buyer.BuyerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{currentUserId}/buyer")
@CrossOrigin(origins = "*")
public class BuyerController {

    private final BuyerService regularBuyer;
    private final BuyerService premiumBuyer;
    private final UserService userService;
    private final ItemMapper itemMapper;

    public BuyerController(BuyerService regularBuyer,
                           @Qualifier("premiumBuyerProxy") BuyerService premiumBuyer,
                           UserService userService,
                           ItemMapper itemMapper) {
        this.regularBuyer = regularBuyer;
        this.premiumBuyer = premiumBuyer;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    @PostMapping("/orderProduct")
    public OrderItemDTO orderProduct(@PathVariable("currentUserId") int buyerId,
                                     @Valid @RequestBody OrderItemDTO orderItemDTO) {

        User buyer = userService.getById(buyerId);
        if (buyer.isPremiumAndNotExpired()) {
            OrderItem orderItem = premiumBuyer.orderProduct(buyer, orderItemDTO);
            return itemMapper.toOrderItemDTO(orderItem);
        }
        OrderItem orderItem = regularBuyer.orderProduct(buyer, orderItemDTO);
        return itemMapper.toOrderItemDTO(orderItem);
    }

    @GetMapping("/getAllOrderedProductsByStatus")
    public List<OrderItemDTO> getAllOrderedProductsByStatus(@PathVariable("currentUserId") int buyerId,
                                                            @RequestParam("orderItemStatus") String orderItemStatus) {

        User buyer = userService.getById(buyerId);
        if (buyer.isPremiumAndNotExpired()) {
            List<OrderItem> orderItems = premiumBuyer.getAllOrderedProductsByStatus(buyer, OrderItem.OrderItemStatus.valueOf(orderItemStatus));
            return orderItems.stream()
                    .map(itemMapper::toOrderItemDTO)
                    .toList();
        }
        List<OrderItem> orderItems = regularBuyer.getAllOrderedProductsByStatus(buyer, OrderItem.OrderItemStatus.valueOf(orderItemStatus));
        return orderItems.stream()
                .map(itemMapper::toOrderItemDTO)
                .toList();
    }

    @DeleteMapping("/cancelOrderItem/{orderItemId}")
    public ResponseEntity<OrderItemDTO> cancelOrderItem(@PathVariable("currentUserId") int buyerId,
                                                        @PathVariable("orderItemId") int orderItemId) {

        User buyer = userService.getById(buyerId);
        OrderItem orderItem = userService.getOrderItemById(orderItemId);
        if (buyer.isPremiumAndNotExpired()) {
            premiumBuyer.cancelOrderItem(buyer, orderItem);
            return ResponseEntity.noContent().build();
        }
        regularBuyer.cancelOrderItem(buyer, orderItem);
        return ResponseEntity.noContent().build();
    }
}
