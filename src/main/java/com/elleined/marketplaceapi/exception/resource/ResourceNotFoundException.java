package com.elleined.marketplaceapi.exception.resource;

import com.elleined.marketplaceapi.exception.resource.ResourceException;

public class ResourceNotFoundException extends ResourceException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
