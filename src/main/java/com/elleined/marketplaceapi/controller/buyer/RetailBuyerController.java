package com.elleined.marketplaceapi.controller.buyer;


import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.mapper.order.RetailOrderMapper;
import com.elleined.marketplaceapi.model.order.Order.Status;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.order.OrderService;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.service.user.buyer.BuyerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/retail/buyers/{currentUserId}/orders")
public class RetailBuyerController {

    private final BuyerService regularBuyer;
    private final BuyerService premiumBuyer;
    private final UserService userService;

    private final OrderService<RetailOrder> retailOrderService;
    private final RetailOrderMapper retailOrderMapper;

    public RetailBuyerController(BuyerService regularBuyer,
                                 @Qualifier("premiumBuyerProxy") BuyerService premiumBuyer,
                                 UserService userService,
                                 OrderService<RetailOrder> retailOrderService,
                                 RetailOrderMapper retailOrderMapper) {
        this.regularBuyer = regularBuyer;
        this.premiumBuyer = premiumBuyer;
        this.userService = userService;
        this.retailOrderService = retailOrderService;
        this.retailOrderMapper = retailOrderMapper;
    }

    @PostMapping
    public RetailOrderDTO orderProduct(@PathVariable("currentUserId") int buyerId,
                                       @Valid @RequestBody RetailOrderDTO dto) {

        User buyer = userService.getById(buyerId);
        if (buyer.isPremiumAndNotExpired()) {
            RetailOrder retailOrder = premiumBuyer.order(buyer, dto);
            return retailOrderMapper.toDTO(retailOrder);
        }
        RetailOrder retailOrder = regularBuyer.order(buyer, dto);
        return retailOrderMapper.toDTO(retailOrder);
    }

    @GetMapping
    public List<RetailOrderDTO> getAllOrderedProductsByStatus(@PathVariable("currentUserId") int buyerId,
                                                              @RequestParam("orderStatus") String orderStatus) {

        User buyer = userService.getById(buyerId);
        if (buyer.isPremiumAndNotExpired()) {
            List<RetailOrder> retailOrders = retailOrderService.getAllOrderedProductsByStatus(buyer, Status.valueOf(orderStatus));
            return retailOrders.stream()
                    .map(retailOrderMapper::toDTO)
                    .toList();
        }
        List<RetailOrder> retailOrders = retailOrderService.getAllOrderedProductsByStatus(buyer, Status.valueOf(orderStatus));
        return retailOrders.stream()
                .map(retailOrderMapper::toDTO)
                .toList();
    }

    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<RetailOrder> cancelOrder(@PathVariable("currentUserId") int buyerId,
                                                       @PathVariable("orderId") int orderId) {

        User buyer = userService.getById(buyerId);
        RetailOrder retailOrder = retailOrderService.getById(orderId);
        if (buyer.isPremiumAndNotExpired()) {
            premiumBuyer.cancelOrder(buyer, retailOrder);
            return ResponseEntity.noContent().build();
        }
        regularBuyer.cancelOrder(buyer, retailOrder);
        return ResponseEntity.noContent().build();
    }
}
