package org.fintech.pojo;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role;

    public User() {}

    public User(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Sample data
    public static User sampleCustomer() {
        return new User(1, "John Doe", "john@fintech.com", "pass123", "CUSTOMER");
    }
    public static User sampleAdmin() {
        return new User(2, "Jane Admin", "jane@fintech.com", "admin123", "ADMIN");
    }
}

