package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.product.ProductDTO;
import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.mapper.ItemMapper;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.mapper.ShopMapper;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.service.user.seller.SellerGetAllService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users/{currentUserId}/seller")
public class SellerController {

    private final UserService userService;

    private final SellerGetAllService sellerGetAllService;
    private final SellerService regularSeller;
    private final SellerService premiumSeller;

    private final ProductService productService;


    private final ShopMapper shopMapper;
    private final ProductMapper productMapper;
    private final ItemMapper itemMapper;

    public SellerController(UserService userService,
                            SellerGetAllService sellerGetAllService, SellerService regularSeller,
                            @Qualifier("premiumSellerProxy") SellerService premiumSeller,
                            ProductService productService,
                            ShopMapper shopMapper, ProductMapper productMapper,
                            ItemMapper itemMapper) {
        this.userService = userService;
        this.sellerGetAllService = sellerGetAllService;
        this.regularSeller = regularSeller;
        this.premiumSeller = premiumSeller;
        this.productService = productService;
        this.shopMapper = shopMapper;
        this.productMapper = productMapper;
        this.itemMapper = itemMapper;
    }

    @GetMapping("/getAllProductByState")
    public List<ProductDTO> getAllProductByState(@PathVariable("currentUserId") int sellerId,
                                                 @RequestParam("productState") String state) {
        User seller = userService.getById(sellerId);
        return sellerGetAllService.getAllProductByState(seller, Product.State.valueOf(state)).stream()
                .map(productMapper::toDTO)
                .toList();
    }

    @GetMapping("/shop")
    public ShopDTO getShop(@PathVariable("currentUserId") int currentUserId) {
        User seller = userService.getById(currentUserId);
        return shopMapper.toDTO(seller.getShop());
    }

    @PatchMapping("/acceptOrder/{orderItemId}")
    public void acceptOrder(@PathVariable("currentUserId") int sellerId,
                            @PathVariable("orderItemId") int orderItemId,
                            @RequestParam("messageToBuyer") String messageToBuyer) {

        User seller = userService.getById(sellerId);
        OrderItem orderItem = userService.getOrderItemById(orderItemId);

        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.acceptOrder(seller, orderItem, messageToBuyer);
            return;
        }
        regularSeller.acceptOrder(seller, orderItem, messageToBuyer);
    }

    @PatchMapping("/rejectOrder/{orderItemId}")
    public void rejectOrder(@PathVariable("currentUserId") int sellerId,
                            @PathVariable("orderItemId") int orderItemId,
                            @RequestParam("messageToBuyer") String messageToBuyer) {

        User seller = userService.getById(sellerId);
        OrderItem orderItem = userService.getOrderItemById(orderItemId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.rejectOrder(seller, orderItem, messageToBuyer);
            return;
        }
        regularSeller.rejectOrder(seller, orderItem, messageToBuyer);
    }

    @PatchMapping("/soldOrder/{orderItemId}")
    public void soldOrder(@PathVariable("currentUserId") int sellerId,
                          @PathVariable("orderItemId") int orderItemId) {

        User seller = userService.getById(sellerId);
        OrderItem orderItem = userService.getOrderItemById(orderItemId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.soldOrder(seller, orderItem);
            return;
        }
        regularSeller.soldOrder(seller, orderItem);
    }

    @GetMapping("/getAllSellerProductOrderByStatus")
    public List<OrderItemDTO> getAllSellerProductOrderByStatus(@PathVariable("currentUserId") int sellerId,
                                                               @RequestParam("orderItemStatus") String orderItemStatus) {

        User seller = userService.getById(sellerId);
        return sellerGetAllService.getAllSellerProductOrderByStatus(seller, OrderItem.OrderItemStatus.valueOf(orderItemStatus)).stream()
                .map(itemMapper::toOrderItemDTO)
                .toList();
    }

    @PostMapping("/saveProduct")
    public ProductDTO saveProduct(@PathVariable("currentUserId") int sellerId,
                                  @Valid @RequestPart("productDTO") ProductDTO productDTO,
                                  @RequestPart("productPicture") MultipartFile productPicture) throws IOException {

        User seller = userService.getById(sellerId);
        if (seller.isPremiumAndNotExpired()) {
            Product product = premiumSeller.saveProduct(seller, productDTO, productPicture);
            return productMapper.toDTO(product);
        }

        Product product = regularSeller.saveProduct(seller, productDTO, productPicture);
        return productMapper.toDTO(product);
    }

    @PutMapping("/updateProduct/{productId}")
    public ProductDTO update(@PathVariable("currentUserId") int sellerId,
                             @PathVariable("productId") int productId,
                             @Valid @RequestPart("productDTO") ProductDTO productDTO,
                             @RequestPart("productPicture") MultipartFile productPicture) throws IOException {

        User seller = userService.getById(sellerId);
        Product product = productService.getById(productId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.updateProduct(seller, product, productDTO, productPicture);
            return productMapper.toDTO(product);
        }
        regularSeller.updateProduct(seller, product, productDTO, productPicture);
        return productMapper.toDTO(product);
    }

    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<ProductDTO> deleteById(@PathVariable("currentUserId") int sellerId,
                                                 @PathVariable("productId") int productId) {

        User seller = userService.getById(sellerId);
        Product product = productService.getById(productId);
        if (seller.isPremiumAndNotExpired()) {
            premiumSeller.deleteProduct(seller, product);
            return ResponseEntity.noContent().build();
        }
        regularSeller.deleteProduct(seller, product);
        return ResponseEntity.noContent().build();
    }
}
