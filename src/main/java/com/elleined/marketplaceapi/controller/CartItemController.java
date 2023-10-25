package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.cart.CartItemDTO;
import com.elleined.marketplaceapi.dto.order.OrderDTO;
import com.elleined.marketplaceapi.model.cart.CartItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.cart.CartItemService;
import com.elleined.marketplaceapi.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{currentUserId}/cartItems")
public class CartItemController {

    private final UserService userService;
    private final CartItemService cartItemService;
    private final ItemMapper itemMapper;
    @GetMapping
    public List<CartItemDTO> getAll(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);

        List<CartItem> cartItems = cartItemService.getAll(currentUser);
        return cartItems.stream()
                .map(itemMapper::toCartItemDTO)
                .toList();
    }

    @PostMapping
    public CartItemDTO save(@PathVariable("currentUserId") int currentUserId,
                            @Valid @RequestBody CartItemDTO cartItemDTO) {

        User currentUser = userService.getById(currentUserId);
        CartItem cartItem = cartItemService.save(currentUser, cartItemDTO);
        return itemMapper.toCartItemDTO(cartItem);
    }

    @DeleteMapping("/{cartItemId}")
    public void delete(@PathVariable("currentUserId") int currentUserId,
                       @PathVariable("cartItemId") int cartItemId) {

        User currentUser = userService.getById(currentUserId);
        CartItem cartItem = cartItemService.getCartItemById(cartItemId);
        cartItemService.delete(currentUser, cartItem);
    }


    @PostMapping("/{cartItemId}/order")
    public OrderDTO moveToOrderItem(@PathVariable("currentUserId") int currentUserId,
                                    @PathVariable("cartItemId") int cartItemId) {

        User currentUser = userService.getById(currentUserId);
        CartItem cartItem = cartItemService.getCartItemById(cartItemId);
        OrderItem orderItem = cartItemService.moveToOrderItem(currentUser, cartItem);

        return itemMapper.toOrderItemDTO(orderItem);
    }


    @PostMapping("/order")
    public List<OrderDTO> moveAllToOrderItem(@PathVariable("currentUserId") int currentUserId,
                                             @RequestBody List<Integer> cartItemIds) {
        User currentUser = userService.getById(currentUserId);
        List<CartItem> cartItems = cartItemService.getAllById(cartItemIds);
        List<OrderItem> orderItems = cartItemService.moveAllToOrderItem(currentUser, cartItems);
        return orderItems.stream()
                .map(itemMapper::toOrderItemDTO)
                .toList();
    }
}
