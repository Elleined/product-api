package com.elleined.marketplaceapi.service.product.wholesale;

import com.elleined.marketplaceapi.dto.product.sale.request.SaleWholeSaleRequest;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.service.product.ProductService;

public interface WholeSaleProductService extends ProductService<WholeSaleProduct> {
    double calculateSalePrice(double totalPrice, int salePercentage);
}
