package com.elleined.marketplaceapi.controller.cart;


import com.elleined.marketplaceapi.dto.cart.RetailCartItemDTO;
import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.mapper.cart.RetailCartItemMapper;
import com.elleined.marketplaceapi.mapper.order.RetailOrderMapper;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.cart.retail.RetailCartItemService;
import com.elleined.marketplaceapi.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{currentUserId}/retail/cart-items")
public class RetailCartController {

    private final UserService userService;
    private final RetailCartItemService retailCartItemService;
    private final RetailCartItemMapper retailCartItemMapper;

    private final RetailOrderMapper retailOrderMapper;

    @PostMapping
    public RetailCartItemDTO save(@PathVariable("currentUserId") int currentUserId,
                                  @Valid @RequestBody RetailCartItemDTO dto) {

        User currentUser = userService.getById(currentUserId);
        RetailCartItem retailCartItem = retailCartItemService.save(currentUser, dto);
        return retailCartItemMapper.toDTO(retailCartItem);
    }

    @GetMapping
    public List<RetailCartItemDTO> getAll(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);

        List<RetailCartItem> cartItems = retailCartItemService.getAll(currentUser);
        return cartItems.stream()
                .map(retailCartItemMapper::toDTO)
                .toList();
    }

    @DeleteMapping("/{cartItemId}")
    public void delete(@PathVariable("currentUserId") int currentUserId,
                       @PathVariable("cartItemId") int cartItemId) {

        User currentUser = userService.getById(currentUserId);
        RetailCartItem retailCartItem = retailCartItemService.getById(cartItemId);
        retailCartItemService.delete(retailCartItem);
    }


    @PostMapping("/{cartItemId}/order")
    public RetailOrderDTO orderCartItem(@PathVariable("currentUserId") int currentUserId,
                                          @PathVariable("cartItemId") int cartItemId) {

        User currentUser = userService.getById(currentUserId);
        RetailCartItem retailCartItem = retailCartItemService.getById(cartItemId);

        RetailOrder retailOrder = retailCartItemService.orderCartItem(currentUser, retailCartItem);
        return retailOrderMapper.toDTO(retailOrder);
    }


    @PostMapping("/order")
    public List<RetailOrderDTO> orderAllCartItems(@PathVariable("currentUserId") int currentUserId,
                                                   @RequestBody List<Integer> cartItemIds) {

        User currentUser = userService.getById(currentUserId);
        List<RetailCartItem> retailCartItems = retailCartItemService.getAllById(cartItemIds);
        return retailCartItemService.orderAllCartItems(currentUser, retailCartItems).stream()
                .map(retailOrderMapper::toDTO)
                .toList();
    }
}
