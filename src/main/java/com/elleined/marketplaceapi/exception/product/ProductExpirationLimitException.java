package com.elleined.marketplaceapi.exception.product;

import com.elleined.marketplaceapi.exception.product.ProductException;

public class ProductExpirationLimitException extends ProductException {
    public ProductExpirationLimitException(String message) {
        super(message);
    }
}
