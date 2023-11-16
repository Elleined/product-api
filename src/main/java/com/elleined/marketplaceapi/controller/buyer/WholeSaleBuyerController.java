package com.elleined.marketplaceapi.controller.buyer;

import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.mapper.order.WholeSaleOrderMapper;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
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
@RequestMapping("/whole-sale/buyers/{currentUserId}/orders")
public class WholeSaleBuyerController {
    private final BuyerService regularBuyer;
    private final BuyerService premiumBuyer;
    private final UserService userService;

    private final OrderService<WholeSaleOrder> wholeSaleOrderService;
    private final WholeSaleOrderMapper wholeSaleOrderMapper;


    public WholeSaleBuyerController(BuyerService regularBuyer,
                                    @Qualifier("premiumBuyerProxy") BuyerService premiumBuyer,
                                    UserService userService,
                                    OrderService<WholeSaleOrder> wholeSaleOrderService,
                                    WholeSaleOrderMapper wholeSaleOrderMapper) {
        this.regularBuyer = regularBuyer;
        this.premiumBuyer = premiumBuyer;
        this.userService = userService;
        this.wholeSaleOrderService = wholeSaleOrderService;
        this.wholeSaleOrderMapper = wholeSaleOrderMapper;
    }

    @PostMapping
    public WholeSaleOrderDTO orderProduct(@PathVariable("currentUserId") int buyerId,
                                          @Valid @RequestBody WholeSaleOrderDTO dto) {

        User buyer = userService.getById(buyerId);
        if (buyer.isPremiumAndNotExpired()) {
            WholeSaleOrder wholeSaleOrder = premiumBuyer.order(buyer, dto);
            return wholeSaleOrderMapper.toDTO(wholeSaleOrder);
        }
        WholeSaleOrder wholeSaleOrder = regularBuyer.order(buyer, dto);
        return wholeSaleOrderMapper.toDTO(wholeSaleOrder);
    }

    @GetMapping
    public List<WholeSaleOrderDTO> getAllOrderedProductsByStatus(@PathVariable("currentUserId") int buyerId,
                                                                 @RequestParam("orderStatus") String orderStatus) {

        User buyer = userService.getById(buyerId);
        if (buyer.isPremiumAndNotExpired()) {
            List<WholeSaleOrder> wholeSaleOrders = wholeSaleOrderService.getAllOrderedProductsByStatus(buyer, Order.Status.valueOf(orderStatus));
            return wholeSaleOrders.stream()
                    .map(wholeSaleOrderMapper::toDTO)
                    .toList();
        }
        List<WholeSaleOrder> wholeSaleOrders = wholeSaleOrderService.getAllOrderedProductsByStatus(buyer, Order.Status.valueOf(orderStatus));
        return wholeSaleOrders.stream()
                .map(wholeSaleOrderMapper::toDTO)
                .toList();
    }

    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<WholeSaleOrder> cancelOrder(@PathVariable("currentUserId") int buyerId,
                                                          @PathVariable("orderId") int orderId) {

        User buyer = userService.getById(buyerId);
        WholeSaleOrder wholeSaleOrder = wholeSaleOrderService.getById(orderId);
        if (buyer.isPremiumAndNotExpired()) {
            premiumBuyer.cancelOrder(buyer, wholeSaleOrder);
            return ResponseEntity.noContent().build();
        }
        regularBuyer.cancelOrder(buyer, wholeSaleOrder);
        return ResponseEntity.noContent().build();
    }
}
