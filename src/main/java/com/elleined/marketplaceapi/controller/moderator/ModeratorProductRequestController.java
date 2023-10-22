package com.elleined.marketplaceapi.controller.moderator;

import com.elleined.marketplaceapi.dto.product.ProductDTO;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.service.email.EmailService;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moderators/{moderatorId}/pending-products")
public class ModeratorProductRequestController {

    private final ModeratorService moderatorService;

    private final ProductService productService;
    private final ProductMapper productMapper;

    private final EmailService emailService;

    @GetMapping
    public List<ProductDTO> getAllPendingProduct() {
        return moderatorService.getAllPendingProduct().stream()
                .map(productMapper::toDTO)
                .toList();
    }

    @PatchMapping("/{productToBeListedId}/list")
    public void listProduct(@PathVariable("moderatorId") int moderatorId,
                            @PathVariable("productToBeListedId") int productToBeListedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Product product = productService.getById(productToBeListedId);
        moderatorService.listProduct(moderator, product);
        emailService.sendProductListedEmail(product.getSeller(), product);
    }

    @PatchMapping("/list")
    public void listAllProduct(@PathVariable("moderatorId") int moderatorId,
                               @RequestBody Set<Integer> productsToBeListedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<Product> productsToBeListed = productService.getAllById(productsToBeListedId);
        moderatorService.listAllProduct(moderator, productsToBeListed);
    }

    @PatchMapping("/{productToBeRejectedId}/reject")
    public void rejectProduct(@PathVariable("moderatorId") int moderatorId,
                              @PathVariable("productToBeRejectedId") int productToBeRejectedId,
                              @RequestParam("reason") String reason) {

        if (StringUtil.isNotValid(reason)) throw new NotValidBodyException("Please provide the reason why you are rejecting this product...");

        Moderator moderator = moderatorService.getById(moderatorId);
        Product productToBeRejected = productService.getById(productToBeRejectedId);
        moderatorService.rejectProduct(moderator, productToBeRejected);
        emailService.sendRejectedProductEmail(productToBeRejected, reason);
    }

    @PatchMapping("/reject")
    public void rejectAllProduct(@PathVariable("moderatorId") int moderatorId,
                                 @RequestParam("reason") String reason,
                                 @RequestBody Set<Integer> productToBeRejectedIds) {

        if (StringUtil.isNotValid(reason)) throw new NotValidBodyException("Please provide the reason why you are rejecting all this product...");

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<Product> productsToBeRejected = productService.getAllById(productToBeRejectedIds);
        moderatorService.rejectAllProduct(moderator, productsToBeRejected);
        productsToBeRejected.forEach(rejectedProduct -> emailService.sendRejectedProductEmail(rejectedProduct, reason));
    }
}
