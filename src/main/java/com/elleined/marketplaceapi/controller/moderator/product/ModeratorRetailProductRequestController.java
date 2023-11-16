package com.elleined.marketplaceapi.controller.moderator.product;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.mapper.product.RetailProductMapper;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.service.email.EmailService;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moderators/{moderatorId}/retail-products")
public class ModeratorRetailProductRequestController {
    private final ModeratorService moderatorService;

    private final RetailProductService retailProductService;
    private final RetailProductMapper retailProductMapper;

    private final EmailService emailService;

    @GetMapping
    public List<RetailProductDTO> getAllPendingProduct() {
        return moderatorService.getAllPendingRetailProduct().stream()
                .map(p -> {
                    double price = retailProductService.calculateTotalPrice(p);
                    return retailProductMapper.toDTO(p, price);
                }).toList();
    }

    @PatchMapping("/{productToBeListedId}/list")
    public void listProduct(@PathVariable("moderatorId") int moderatorId,
                            @PathVariable("productToBeListedId") int productToBeListedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        RetailProduct retailProduct = retailProductService.getById(productToBeListedId);
        moderatorService.listRetailProduct(moderator, retailProduct);
        emailService.sendProductListedEmail(retailProduct.getSeller(), retailProduct);
    }

    @PatchMapping("/list")
    public void listAllProduct(@PathVariable("moderatorId") int moderatorId,
                               @RequestBody Set<Integer> productsToBeListedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<RetailProduct> productsToBeListed = retailProductService.getAllById(productsToBeListedId);
        moderatorService.listAllRetailProduct(moderator, productsToBeListed);
    }

    @PatchMapping("/{productToBeRejectedId}/reject")
    public void rejectProduct(@PathVariable("moderatorId") int moderatorId,
                              @PathVariable("productToBeRejectedId") int productToBeRejectedId,
                              @RequestParam("reason") String reason) {

        if (StringUtil.isNotValid(reason)) throw new NotValidBodyException("Please provide the reason why you are rejecting this product...");

        Moderator moderator = moderatorService.getById(moderatorId);
        RetailProduct retailProductToBeRejected = retailProductService.getById(productToBeRejectedId);
        moderatorService.rejectRetailProduct(moderator, retailProductToBeRejected);
        emailService.sendRejectedProductEmail(retailProductToBeRejected, reason);
    }

    @PatchMapping("/reject")
    public void rejectAllProduct(@PathVariable("moderatorId") int moderatorId,
                                 @RequestParam("reason") String reason,
                                 @RequestBody Set<Integer> productToBeRejectedIds) {

        if (StringUtil.isNotValid(reason)) throw new NotValidBodyException("Please provide the reason why you are rejecting all this product...");

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<RetailProduct> productsToBeRejected = retailProductService.getAllById(productToBeRejectedIds);
        moderatorService.rejectAllRetailProduct(moderator, productsToBeRejected);
        productsToBeRejected.forEach(rejectedProduct -> emailService.sendRejectedProductEmail(rejectedProduct, reason));
    }
}
