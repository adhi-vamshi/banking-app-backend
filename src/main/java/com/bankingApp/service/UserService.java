package com.bankingApp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankingApp.model.ChangePasswordRequest;
import com.bankingApp.model.User;
import com.bankingApp.repository.UserRepository;
import com.bankingApp.utility.PasswordValidator;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User registerUser(User user) throws Exception {
        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new Exception("Username already exists");
        }
        if (userRepository.existsByEmailId(user.getEmailId())) {
            throw new Exception("Email already exists");
        }
        
        // Validate password
        if (!PasswordValidator.isValid(user.getPassword())) {
            throw new Exception("Password must be 8-16 characters with at least one uppercase, one number, and one special character");
        }
        
        return userRepository.save(user);
    }
    
    public User login(String username, String password) throws Exception {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            throw new Exception("Invalid username or password");
        }
        return userOpt.get();
    }
    
    public boolean changePassword(String username, ChangePasswordRequest request) throws Exception {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found");
        }
        
        User user = userOpt.get();
        
        // Verify current password
        if (!user.getPassword().equals(request.getCurrentPassword())) {
            throw new Exception("Current password is incorrect");
        }
        
        // Validate new password
        if (!PasswordValidator.isValid(request.getNewPassword())) {
            throw new Exception("Password must be 8-16 characters with at least one uppercase, one number, and one special character");
        }
        
        // Check if passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new Exception("New passwords do not match");
        }
        
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return true;
    }
    
    public Double getBalance(String accountNumber) throws Exception {
        Optional<User> userOpt = userRepository.findByAccountNumber(accountNumber);
        if (userOpt.isEmpty()) {
            throw new Exception("Account not found");
        }
        return userOpt.get().getBalance();
    }
    
    public User deposit(String accountNumber, Double amount) throws Exception {
        Optional<User> userOpt = userRepository.findByAccountNumber(accountNumber);
        if (userOpt.isEmpty()) {
            throw new Exception("Account not found");
        }
        
        User user = userOpt.get();
        user.setBalance(user.getBalance() + amount);
        return userRepository.save(user);
    }
    
    public User withdraw(String accountNumber, Double amount) throws Exception {
        Optional<User> userOpt = userRepository.findByAccountNumber(accountNumber);
        if (userOpt.isEmpty()) {
            throw new Exception("Account not found");
        }
        
        User user = userOpt.get();
        if (user.getBalance() < amount) {
            throw new Exception("Insufficient balance");
        }
        
        user.setBalance(user.getBalance() - amount);
        return userRepository.save(user);
    }
    
    @Transactional
    public void transfer(String fromAccount, String toAccount, Double amount) throws Exception {
        // Check if both accounts exist
        Optional<User> fromUserOpt = userRepository.findByAccountNumber(fromAccount);
        Optional<User> toUserOpt = userRepository.findByAccountNumber(toAccount);
        
        if (fromUserOpt.isEmpty()) {
            throw new Exception("Sender account not found");
        }
        if (toUserOpt.isEmpty()) {
            throw new Exception("Recipient account not found");
        }
        
        User fromUser = fromUserOpt.get();
        User toUser = toUserOpt.get();
        
        // Check sufficient balance
        if (fromUser.getBalance() < amount) {
            throw new Exception("Insufficient balance");
        }
        
        // Perform transfer
        fromUser.setBalance(fromUser.getBalance() - amount);
        toUser.setBalance(toUser.getBalance() + amount);
        
        userRepository.save(fromUser);
        userRepository.save(toUser);
    }
    
    public boolean accountExists(String accountNumber) {
        return userRepository.existsByAccountNumber(accountNumber);
    }
}