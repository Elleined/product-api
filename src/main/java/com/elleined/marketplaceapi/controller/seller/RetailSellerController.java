package com.elleined.marketplaceapi.controller.seller;


import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.mapper.order.RetailOrderMapper;
import com.elleined.marketplaceapi.mapper.product.RetailProductMapper;
import com.elleined.marketplaceapi.model.order.Order.Status;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.Product.State;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.order.OrderService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/retail/sellers/{currentUserId}/products")
public class RetailSellerController {
    private final UserService userService;
    private final SellerService regularSeller;
    private final SellerService premiumSeller;

    private final OrderService<RetailOrder> retailOrderService;
    private final RetailOrderMapper retailOrderMapper;

    private final RetailProductService retailProductService;
    private final RetailProductMapper retailProductMapper;

    public RetailSellerController(UserService userService,
                            SellerService regularSeller,
                            @Qualifier("premiumSellerProxy") SellerService premiumSeller,
                            OrderService<RetailOrder> retailOrderService,
                            RetailOrderMapper retailOrderMapper, RetailProductService retailProductService,
                            RetailProductMapper retailProductMapper) {
        this.userService = userService;
        this.regularSeller = regularSeller;
        this.premiumSeller = premiumSeller;
        this.retailOrderService = retailOrderService;
        this.retailOrderMapper = retailOrderMapper;
        this.retailProductService = retailProductService;
        this.retailProductMapper = retailProductMapper;
    }

    @GetMapping("/orders")
    public List<RetailOrderDTO> getAllSellerProductOrderByStatus(@PathVariable("currentUserId") int sellerId,
                                                                 @RequestParam("orderStatus") String orderStatus) {

        User seller = userService.getById(sellerId);
        return retailOrderService.getAllProductOrderByStatus(seller, Status.valueOf(orderStatus)).stream()
                .map(retailOrderMapper::toDTO)
                .toList();
    }

    @PatchMapping("/orders/{orderId}/accept")
    public void acceptOrder(@PathVariable("currentUserId") int sellerId,
                            @PathVariable("orderId") int orderId,
                            @RequestParam("messageToBuyer") String messageToBuyer) {

        User seller = userService.getById(sellerId);
        RetailOrder retailOrder = retailOrderService.getById(orderId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.acceptOrder(seller, retailOrder, messageToBuyer);
            return;
        }
        regularSeller.acceptOrder(seller, retailOrder, messageToBuyer);
    }

    @PatchMapping("/orders/{orderId}/reject")
    public void rejectOrder(@PathVariable("currentUserId") int sellerId,
                            @PathVariable("orderId") int orderId,
                            @RequestParam("messageToBuyer") String messageToBuyer) {

        User seller = userService.getById(sellerId);
        RetailOrder retailOrder = retailOrderService.getById(orderId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.rejectOrder(seller, retailOrder, messageToBuyer);
            return;
        }
        regularSeller.rejectOrder(seller, retailOrder, messageToBuyer);
    }

    @PatchMapping("/orders/{orderId}/sold")
    public void soldOrder(@PathVariable("currentUserId") int sellerId,
                          @PathVariable("orderId") int orderId) {

        User seller = userService.getById(sellerId);
        RetailOrder retailOrder = retailOrderService.getById(orderId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.soldOrder(seller, retailOrder);
            return;
        }
        regularSeller.soldOrder(seller, retailOrder);
    }

    @GetMapping
    public List<RetailProductDTO> getAllProductByState(@PathVariable("currentUserId") int sellerId,
                                                       @RequestParam("productState") String state) {
        User seller = userService.getById(sellerId);
        return retailProductService.getAllByState(seller, State.valueOf(state)).stream()
                .map(p -> {
                    double price = retailProductService.calculateTotalPrice(p);
                    return retailProductMapper.toDTO(p, price);
                }).toList();
    }

    @PostMapping
    public RetailProductDTO saveProduct(@PathVariable("currentUserId") int sellerId,
                                        @Valid @RequestPart("productDTO") RetailProductDTO dto,
                                        @RequestPart("productPicture") MultipartFile productPicture) throws IOException {

        User seller = userService.getById(sellerId);
        if (seller.isPremiumAndNotExpired()) {
            RetailProduct retailProduct = premiumSeller.saveProduct(seller, dto, productPicture);
            double price = retailProductService.calculateTotalPrice(retailProduct);
            return retailProductMapper.toDTO(retailProduct, price);
        }

        RetailProduct retailProduct = regularSeller.saveProduct(seller, dto, productPicture);
        double price = retailProductService.calculateTotalPrice(retailProduct);
        return retailProductMapper.toDTO(retailProduct, price);
    }

    @PutMapping("/{productId}")
    public RetailProductDTO update(@PathVariable("currentUserId") int sellerId,
                                   @PathVariable("productId") int productId,
                                   @Valid @RequestPart("productDTO") RetailProductDTO dto,
                                   @RequestPart("productPicture") MultipartFile productPicture) throws IOException {

        User seller = userService.getById(sellerId);
        RetailProduct retailProduct = retailProductService.getById(productId);
        if (seller.isPremiumAndNotExpired()) {
            RetailProduct updatedRetailProduct = premiumSeller.updateProduct(seller, retailProduct, dto, productPicture);
            double price = retailProductService.calculateTotalPrice(updatedRetailProduct);
            return retailProductMapper.toDTO(updatedRetailProduct, price);
        }

        RetailProduct updatedRetailProduct = regularSeller.updateProduct(seller, retailProduct, dto, productPicture);
        double price = retailProductService.calculateTotalPrice(updatedRetailProduct);
        return retailProductMapper.toDTO(updatedRetailProduct, price);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<RetailProductDTO> deleteById(@PathVariable("currentUserId") int sellerId,
                                                 @PathVariable("productId") int productId) {

        User seller = userService.getById(sellerId);
        RetailProduct retailProduct = retailProductService.getById(productId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.deleteProduct(seller, retailProduct);
            return ResponseEntity.noContent().build();
        }
        regularSeller.deleteProduct(seller, retailProduct);
        return ResponseEntity.noContent().build();
    }
}
