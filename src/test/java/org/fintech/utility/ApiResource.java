package org.fintech.utility;

public enum ApiResource {
    USERS("/users"),
    ACCOUNTS("/accounts");

    private final String resourcePath;

    ApiResource(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }
}

