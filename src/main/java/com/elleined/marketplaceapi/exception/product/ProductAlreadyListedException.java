package com.elleined.marketplaceapi.exception.product;

import com.elleined.marketplaceapi.exception.product.ProductException;

public class ProductAlreadyListedException extends ProductException {

    public ProductAlreadyListedException(String message) {
        super(message);
    }
}
