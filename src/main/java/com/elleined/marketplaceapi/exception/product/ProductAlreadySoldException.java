package com.elleined.marketplaceapi.exception.product;

public class ProductAlreadySoldException extends ProductException {
    public ProductAlreadySoldException(String message) {
        super(message);
    }
}
