package com.elleined.marketplaceapi.controller.seller;

import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.mapper.order.WholeSaleOrderMapper;
import com.elleined.marketplaceapi.mapper.product.WholeSaleProductMapper;
import com.elleined.marketplaceapi.model.order.Order.Status;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.order.OrderService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/whole-sale/sellers/{currentUserId}/products")
public class WholeSaleSellerController {

    private final UserService userService;
    private final SellerService regularSeller;
    private final SellerService premiumSeller;

    private final OrderService<WholeSaleOrder> wholeSaleOrderService;
    private final WholeSaleOrderMapper wholeSaleOrderMapper;

    private final WholeSaleProductService wholeSaleProductService;
    private final WholeSaleProductMapper wholeSaleProductMapper;

    public WholeSaleSellerController(UserService userService,
                                     SellerService regularSeller,
                                     @Qualifier("premiumSellerProxy") SellerService premiumSeller,
                                     OrderService<WholeSaleOrder> wholeSaleOrderService,
                                     WholeSaleOrderMapper wholeSaleOrderMapper,
                                     WholeSaleProductService wholeSaleProductService,
                                     WholeSaleProductMapper wholeSaleProductMapper) {
        this.userService = userService;
        this.regularSeller = regularSeller;
        this.premiumSeller = premiumSeller;
        this.wholeSaleOrderService = wholeSaleOrderService;
        this.wholeSaleOrderMapper = wholeSaleOrderMapper;
        this.wholeSaleProductService = wholeSaleProductService;
        this.wholeSaleProductMapper = wholeSaleProductMapper;
    }

    @PatchMapping("/{productId}/sale")
    public WholeSaleProductDTO saleProduct(@PathVariable("currentUserId") int sellerId,
                                           @PathVariable("productId") int productId,
                                           @RequestParam("salePrice") double salePrice) {

        User seller = userService.getById(sellerId);
        WholeSaleProduct wholeSaleProduct = wholeSaleProductService.getById(productId);
        if (seller.isPremiumAndNotExpired()) {
            WholeSaleProduct saleProduct = premiumSeller.saleProduct(seller, wholeSaleProduct, salePrice);
            return wholeSaleProductMapper.toDTO(saleProduct);
        }

        WholeSaleProduct saleProduct = regularSeller.saleProduct(seller, wholeSaleProduct, salePrice);
        return wholeSaleProductMapper.toDTO(saleProduct);
    }


    @GetMapping("/orders")
    public List<WholeSaleOrderDTO> getAllSellerProductOrderByStatus(@PathVariable("currentUserId") int sellerId,
                                                                    @RequestParam("orderStatus") String orderStatus) {

        User seller = userService.getById(sellerId);
        return wholeSaleOrderService.getAllProductOrderByStatus(seller, Status.valueOf(orderStatus)).stream()
                .map(wholeSaleOrderMapper::toDTO)
                .toList();
    }

    @PatchMapping("/orders/{orderId}/accept")
    public void acceptOrder(@PathVariable("currentUserId") int sellerId,
                            @PathVariable("orderId") int orderId,
                            @RequestParam("messageToBuyer") String messageToBuyer) {

        User seller = userService.getById(sellerId);
        WholeSaleOrder wholeSaleOrder = wholeSaleOrderService.getById(orderId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.acceptOrder(seller, wholeSaleOrder, messageToBuyer);
            return;
        }
        regularSeller.acceptOrder(seller, wholeSaleOrder, messageToBuyer);
    }

    @PatchMapping("/orders/{orderId}/reject")
    public void rejectOrder(@PathVariable("currentUserId") int sellerId,
                            @PathVariable("orderId") int orderId,
                            @RequestParam("messageToBuyer") String messageToBuyer) {

        User seller = userService.getById(sellerId);
        WholeSaleOrder wholeSaleOrder = wholeSaleOrderService.getById(orderId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.rejectOrder(seller, wholeSaleOrder, messageToBuyer);
            return;
        }
        regularSeller.rejectOrder(seller, wholeSaleOrder, messageToBuyer);
    }

    @PatchMapping("/orders/{orderId}/sold")
    public void soldOrder(@PathVariable("currentUserId") int sellerId,
                          @PathVariable("orderId") int orderId) {

        User seller = userService.getById(sellerId);
        WholeSaleOrder wholeSaleOrder = wholeSaleOrderService.getById(orderId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.soldOrder(seller, wholeSaleOrder);
            return;
        }
        regularSeller.soldOrder(seller, wholeSaleOrder);
    }

    @GetMapping
    public List<WholeSaleProductDTO> getAllProductByState(@PathVariable("currentUserId") int sellerId,
                                                          @RequestParam("productState") String state) {
        User seller = userService.getById(sellerId);
        return wholeSaleProductService.getAllByState(seller, Product.State.valueOf(state)).stream()
                .map(wholeSaleProductMapper::toDTO)
                .toList();
    }

    @PostMapping
    public WholeSaleProductDTO saveProduct(@PathVariable("currentUserId") int sellerId,
                                        @Valid @RequestPart("productDTO") WholeSaleProductDTO dto,
                                        @RequestPart("productPicture") MultipartFile productPicture) throws IOException {

        User seller = userService.getById(sellerId);
        if (seller.isPremiumAndNotExpired()) {
            WholeSaleProduct wholeSaleProduct = premiumSeller.saveProduct(seller, dto, productPicture);
            return wholeSaleProductMapper.toDTO(wholeSaleProduct);
        }

        WholeSaleProduct wholeSaleProduct = regularSeller.saveProduct(seller, dto, productPicture);
        return wholeSaleProductMapper.toDTO(wholeSaleProduct);
    }

    @PutMapping("/{productId}")
    public WholeSaleProductDTO update(@PathVariable("currentUserId") int sellerId,
                                   @PathVariable("productId") int productId,
                                   @Valid @RequestPart("productDTO") WholeSaleProductDTO dto,
                                   @RequestPart("productPicture") MultipartFile productPicture) throws IOException {

        User seller = userService.getById(sellerId);
        WholeSaleProduct wholeSaleProduct = wholeSaleProductService.getById(productId);
        if (seller.isPremiumAndNotExpired()) {
            WholeSaleProduct updatedWholeSaleProduct = premiumSeller.updateProduct(seller, wholeSaleProduct, dto, productPicture);
            return wholeSaleProductMapper.toDTO(updatedWholeSaleProduct);
        }

        WholeSaleProduct updatedWholeSaleProduct = regularSeller.updateProduct(seller, wholeSaleProduct, dto, productPicture);
        return wholeSaleProductMapper.toDTO(updatedWholeSaleProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<WholeSaleProductDTO> deleteById(@PathVariable("currentUserId") int sellerId,
                                                          @PathVariable("productId") int productId) {

        User seller = userService.getById(sellerId);
        WholeSaleProduct wholeSaleProduct = wholeSaleProductService.getById(productId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.deleteProduct(seller, wholeSaleProduct);
            return ResponseEntity.noContent().build();
        }
        regularSeller.deleteProduct(seller, wholeSaleProduct);
        return ResponseEntity.noContent().build();
    }
}
