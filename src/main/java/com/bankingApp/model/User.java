package com.bankingApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Column(name = "username", unique = true)
    private String username;
    
    @NotBlank(message = "First name is required")
    @Column(name = "firstname")
    private String firstname;
    
    @NotBlank(message = "Last name is required")
    @Column(name = "lastname")
    private String lastname;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(name = "emailId", unique = true)
    private String emailId;
    
    @NotBlank(message = "Password is required")
    @Column(name = "password")
    private String password;
    
    @Column(name = "accountNumber", unique = true)
    private String accountNumber;
    
    @DecimalMin(value = "0.0", message = "Balance cannot be negative")
    @Column(name = "balance")
    private Double balance = 0.0;
    
    @NotBlank(message = "Mobile number is required")
    @Column(name = "mobileNumber")
    private String mobileNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (accountNumber == null) {
            accountNumber = "AC" + (int)(Math.random() * 900000 + 100000);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public User() {}
    
    public User(String username, String firstname, String lastname, String emailId, 
                String password, String mobileNumber) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.emailId = emailId;
        this.password = password;
        this.mobileNumber = mobileNumber;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}