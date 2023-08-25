package com.elleined.marketplaceapi.exception;

public class ProductAlreadySoldException extends ProductException {
    public ProductAlreadySoldException(String message) {
        super(message);
    }
}
