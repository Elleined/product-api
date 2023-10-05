package com.elleined.marketplaceapi.exception;

import com.elleined.marketplaceapi.exception.resource.ResourceException;

public class PathNotValidException extends ResourceException {
    public PathNotValidException(String message) {
        super(message);
    }
}
