package com.example.template.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final String resource;

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found: " + id);
        this.resource = resource;
    }

    public String resource() {
        return resource;
    }
}
