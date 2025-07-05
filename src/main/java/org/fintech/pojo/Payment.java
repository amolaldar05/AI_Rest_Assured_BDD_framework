package org.fintech.pojo;

import java.util.List;

public class Payment {
    private String paymentId;
    private double amount;
    private String currency;
    private String status;
    private PaymentMethod paymentMethod;
    private Customer customer;
    private List<Transaction> transactions;
    private FraudCheck fraudCheck;

    public Payment() {}

    public Payment(String paymentId, double amount, String currency, String status, PaymentMethod paymentMethod, Customer customer, List<Transaction> transactions, FraudCheck fraudCheck) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.customer = customer;
        this.transactions = transactions;
        this.fraudCheck = fraudCheck;
    }

    // Getters and setters
    // ...omitted for brevity...

    // Sample data
    public static Payment samplePayment() {
        return new Payment(
            "PAY123456",
            250.75,
            "USD",
            "SUCCESS",
            new PaymentMethod("CARD", "4111111111111111", "12/30", "John Doe", "VISA"),
            new Customer("CUST001", "John Doe", "john@fintech.com", new Address("123 Main St", "New York", "NY", "10001", "USA")),
            List.of(
                new Transaction("TXN1", 100.00, "INITIATED", "2025-07-05T10:00:00Z"),
                new Transaction("TXN2", 150.75, "COMPLETED", "2025-07-05T10:01:00Z")
            ),
            new FraudCheck(false, "None", List.of("IP_WHITELISTED"))
        );
    }

    // Nested classes for PaymentMethod, Customer, Address, Transaction, FraudCheck
    public static class PaymentMethod {
        private String type;
        private String cardNumber;
        private String expiry;
        private String cardHolder;
        private String network;
        public PaymentMethod() {}
        public PaymentMethod(String type, String cardNumber, String expiry, String cardHolder, String network) {
            this.type = type;
            this.cardNumber = cardNumber;
            this.expiry = expiry;
            this.cardHolder = cardHolder;
            this.network = network;
        }
        // Getters and setters omitted for brevity
    }
    public static class Customer {
        private String customerId;
        private String name;
        private String email;
        private Address address;
        public Customer() {}
        public Customer(String customerId, String name, String email, Address address) {
            this.customerId = customerId;
            this.name = name;
            this.email = email;
            this.address = address;
        }
        // Getters and setters omitted for brevity
    }
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String zip;
        private String country;
        public Address() {}
        public Address(String street, String city, String state, String zip, String country) {
            this.street = street;
            this.city = city;
            this.state = state;
            this.zip = zip;
            this.country = country;
        }
        // Getters and setters
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getZip() { return zip; }
        public void setZip(String zip) { this.zip = zip; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }
    public static class Transaction {
        private String transactionId;
        private double amount;
        private String status;
        private String timestamp;
        public Transaction() {}
        public Transaction(String transactionId, double amount, String status, String timestamp) {
            this.transactionId = transactionId;
            this.amount = amount;
            this.status = status;
            this.timestamp = timestamp;
        }
        // Getters and setters
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
    public static class FraudCheck {
        private boolean flagged;
        private String reason;
        private List<String> checks;
        public FraudCheck() {}
        public FraudCheck(boolean flagged, String reason, List<String> checks) {
            this.flagged = flagged;
            this.reason = reason;
            this.checks = checks;
        }
        // Getters and setters
        public boolean isFlagged() { return flagged; }
        public void setFlagged(boolean flagged) { this.flagged = flagged; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public List<String> getChecks() { return checks; }
        public void setChecks(List<String> checks) { this.checks = checks; }
    }
}
