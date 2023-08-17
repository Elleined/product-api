package com.elleined.marketplaceapi.populator;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class Populator {
    protected final ObjectMapper objectMapper;

    public Populator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public abstract void populate(final String jsonFile) throws IOException;
}
