package com.elleined.marketplaceapi.controller.cart;

import com.elleined.marketplaceapi.dto.cart.WholeSaleCartItemDTO;
import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.mapper.cart.WholeSaleCartItemMapper;
import com.elleined.marketplaceapi.mapper.order.WholeSaleOrderMapper;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.cart.wholesale.WholeSaleCartItemService;
import com.elleined.marketplaceapi.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{currentUserId}/whole-sale/cart-items")
public class WholeSaleCartController {

    private final UserService userService;
    private final WholeSaleCartItemService wholeSaleCartItemService;
    private final WholeSaleCartItemMapper wholeSaleCartItemMapper;

    private final WholeSaleOrderMapper wholeSaleOrderMapper;

    @PostMapping
    public WholeSaleCartItemDTO save(@PathVariable("currentUserId") int currentUserId,
                                  @Valid @RequestBody WholeSaleCartItemDTO dto) {

        User currentUser = userService.getById(currentUserId);
        WholeSaleCartItem wholeSaleCartItem = wholeSaleCartItemService.save(currentUser, dto);
        return wholeSaleCartItemMapper.toDTO(wholeSaleCartItem);
    }

    @GetMapping
    public List<WholeSaleCartItemDTO> getAll(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);

        List<WholeSaleCartItem> cartItems = wholeSaleCartItemService.getAll(currentUser);
        return cartItems.stream()
                .map(wholeSaleCartItemMapper::toDTO)
                .toList();
    }

    @DeleteMapping("/{cartItemId}")
    public void delete(@PathVariable("currentUserId") int currentUserId,
                       @PathVariable("cartItemId") int cartItemId) {

        User currentUser = userService.getById(currentUserId);
        WholeSaleCartItem wholeSaleCartItem = wholeSaleCartItemService.getById(cartItemId);
        wholeSaleCartItemService.delete(wholeSaleCartItem);
    }


    @PostMapping("/{cartItemId}/order")
    public WholeSaleOrderDTO orderCartItem(@PathVariable("currentUserId") int currentUserId,
                                           @PathVariable("cartItemId") int cartItemId) {

        User currentUser = userService.getById(currentUserId);
        WholeSaleCartItem wholeSaleCartItem = wholeSaleCartItemService.getById(cartItemId);

        WholeSaleOrder wholeSaleOrder = wholeSaleCartItemService.orderCartItem(currentUser, wholeSaleCartItem);
        return wholeSaleOrderMapper.toDTO(wholeSaleOrder);
    }


    @PostMapping("/order")
    public List<WholeSaleOrderDTO> orderAllCartItems(@PathVariable("currentUserId") int currentUserId,
                                                  @RequestBody List<Integer> cartItemIds) {

        User currentUser = userService.getById(currentUserId);
        List<WholeSaleCartItem> wholeSaleCartItems = wholeSaleCartItemService.getAllById(cartItemIds);
        return wholeSaleCartItemService.orderAllCartItems(currentUser, wholeSaleCartItems).stream()
                .map(wholeSaleOrderMapper::toDTO)
                .toList();
    }

}
