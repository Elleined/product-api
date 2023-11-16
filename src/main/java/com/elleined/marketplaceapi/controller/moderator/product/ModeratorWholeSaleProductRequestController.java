package com.elleined.marketplaceapi.controller.moderator.product;

import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.mapper.product.WholeSaleProductMapper;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.service.email.EmailService;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moderators/{moderatorId}/whole-sale-products")
public class ModeratorWholeSaleProductRequestController {

    private final ModeratorService moderatorService;

    private final WholeSaleProductService wholeSaleProductService;
    private final WholeSaleProductMapper wholeSaleProductMapper;

    private final EmailService emailService;

    @GetMapping
    public List<WholeSaleProductDTO> getAllPendingProduct() {
        return moderatorService.getAllPendingWholeSaleProduct().stream()
                .map(wholeSaleProductMapper::toDTO)
                .toList();
    }

    @PatchMapping("/{productToBeListedId}/list")
    public void listProduct(@PathVariable("moderatorId") int moderatorId,
                            @PathVariable("productToBeListedId") int productToBeListedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        WholeSaleProduct wholeSaleProduct = wholeSaleProductService.getById(productToBeListedId);
        moderatorService.listWholeSaleProduct(moderator, wholeSaleProduct);
        emailService.sendProductListedEmail(wholeSaleProduct.getSeller(), wholeSaleProduct);
    }

    @PatchMapping("/list")
    public void listAllProduct(@PathVariable("moderatorId") int moderatorId,
                               @RequestBody Set<Integer> productsToBeListedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<WholeSaleProduct> productsToBeListed = wholeSaleProductService.getAllById(productsToBeListedId);
        moderatorService.listAllWholeSaleProduct(moderator, productsToBeListed);
    }

    @PatchMapping("/{productToBeRejectedId}/reject")
    public void rejectProduct(@PathVariable("moderatorId") int moderatorId,
                              @PathVariable("productToBeRejectedId") int productToBeRejectedId,
                              @RequestParam("reason") String reason) {

        if (StringUtil.isNotValid(reason)) throw new NotValidBodyException("Please provide the reason why you are rejecting this product...");

        Moderator moderator = moderatorService.getById(moderatorId);
        WholeSaleProduct wholeSaleProductToBeRejected = wholeSaleProductService.getById(productToBeRejectedId);
        moderatorService.rejectWholeSaleProduct(moderator, wholeSaleProductToBeRejected);
        emailService.sendRejectedProductEmail(wholeSaleProductToBeRejected, reason);
    }

    @PatchMapping("/reject")
    public void rejectAllProduct(@PathVariable("moderatorId") int moderatorId,
                                 @RequestParam("reason") String reason,
                                 @RequestBody Set<Integer> productToBeRejectedIds) {

        if (StringUtil.isNotValid(reason)) throw new NotValidBodyException("Please provide the reason why you are rejecting all this product...");

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<WholeSaleProduct> productsToBeRejected = wholeSaleProductService.getAllById(productToBeRejectedIds);
        moderatorService.rejectAllWholeSaleProduct(moderator, productsToBeRejected);
        productsToBeRejected.forEach(rejectedProduct -> emailService.sendRejectedProductEmail(rejectedProduct, reason));
    }
}
