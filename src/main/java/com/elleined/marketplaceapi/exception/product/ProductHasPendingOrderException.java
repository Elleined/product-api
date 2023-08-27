package com.elleined.marketplaceapi.exception.product;

public class ProductHasPendingOrderException extends ProductException {
    public ProductHasPendingOrderException(String message) {
        super(message);
    }
}
