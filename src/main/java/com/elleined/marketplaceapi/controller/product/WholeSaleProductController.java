package com.elleined.marketplaceapi.controller.product;

import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.exception.product.ProductPriceException;
import com.elleined.marketplaceapi.mapper.product.WholeSaleProductMapper;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import com.elleined.marketplaceapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/whole-sale-products")
public class WholeSaleProductController {
    private final WholeSaleProductService wholeSaleProductService;
    private final WholeSaleProductMapper wholeSaleProductMapper;

    private final UserService userService;

    @GetMapping("/listed/{currentUserId}")
    public List<WholeSaleProductDTO> getAllExcept(@PathVariable("currentUserId") int currentUserId) {
        User currentUser = userService.getById(currentUserId);
        return wholeSaleProductService.getAllExcept(currentUser).stream()
                .map(wholeSaleProductMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public WholeSaleProductDTO getById(@PathVariable("id") int id) {
        WholeSaleProduct wholeSaleProduct = wholeSaleProductService.getById(id);
        return wholeSaleProductMapper.toDTO(wholeSaleProduct);
    }

    @GetMapping("/search-by-crop-name")
    public List<WholeSaleProductDTO> searchProductByCropName(@RequestParam("cropName") String cropName) {
        return wholeSaleProductService.searchProductByCropName(cropName).stream()
                .map(wholeSaleProductMapper::toDTO)
                .toList();
    }

    @GetMapping("/{productId}/calculate-sale-percentage")
    public double getSalePercentage(@PathVariable("productId") int productId,
                                    @RequestParam("salePrice") int salePrice) {
        WholeSaleProduct wholeSaleProduct = wholeSaleProductService.getById(productId);
        return wholeSaleProductService.getSalePercentage(wholeSaleProduct.getPrice().doubleValue(), salePrice);
    }
}
