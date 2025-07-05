package org.fintech.utility;

public enum ApiResource {
    // User and Account Endpoints
    USERS("/users"),
    ACCOUNTS("/accounts"),

    // Payment Gateway Endpoints
    PAYMENT_INITIATE("/api/payments/initiate"),
    PAYMENT_STATUS("/api/payments/status"),
    PAYMENT_WEBHOOK("/api/payments/webhook"),
    PAYMENT_MODES("/api/payments/modes"),
    PAYMENT_3DS("/api/payments/3ds");

    private final String resourcePath;

    ApiResource(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
