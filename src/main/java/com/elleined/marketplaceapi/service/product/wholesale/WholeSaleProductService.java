package com.elleined.marketplaceapi.service.product.wholesale;

import com.elleined.marketplaceapi.dto.product.sale.SaleWholeSaleRequest;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.service.product.ProductService;

public interface WholeSaleProductService extends ProductService<WholeSaleProduct> {
    double calculateTotalPriceByPercentage(SaleWholeSaleRequest saleWholeSaleRequest);
    double calculateTotalPriceByPercentage(double totalPrice, int salePercentage);
}
