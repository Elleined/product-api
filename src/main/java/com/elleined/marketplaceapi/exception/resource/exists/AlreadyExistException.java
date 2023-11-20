package com.elleined.marketplaceapi.exception.resource.exists;

import com.elleined.marketplaceapi.exception.resource.ResourceException;

public class AlreadyExistException extends ResourceException {
    public AlreadyExistException(String message) {
        super(message);
    }
}
