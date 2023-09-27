package com.elleined.marketplaceapi.service.validator;

public interface Validator<T> {
    void validate(T t);
}
