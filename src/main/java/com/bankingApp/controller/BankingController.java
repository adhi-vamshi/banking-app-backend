package com.bankingApp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankingApp.model.ChangePasswordRequest;
import com.bankingApp.model.TransactionRequest;
import com.bankingApp.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/banking")
@CrossOrigin(origins = "http://localhost:3000")
public class BankingController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<?> getBalance(@PathVariable String accountNumber) {
        try {
            Double balance = userService.getBalance(accountNumber);
            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody TransactionRequest request) {
        try {
            userService.deposit(request.getAccountNumber(), request.getAmount());
            Double newBalance = userService.getBalance(request.getAccountNumber());
            return ResponseEntity.ok(Map.of(
                "message", "Deposit successful",
                "newBalance", newBalance
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody TransactionRequest request) {
        try {
            userService.withdraw(request.getAccountNumber(), request.getAmount());
            Double newBalance = userService.getBalance(request.getAccountNumber());
            return ResponseEntity.ok(Map.of(
                "message", "Withdrawal successful",
                "newBalance", newBalance
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransactionRequest request) {
        try {
            userService.transfer(request.getAccountNumber(), request.getToAccount(), request.getAmount());
            Double newBalance = userService.getBalance(request.getAccountNumber());
            return ResponseEntity.ok(Map.of(
                "message", "Transfer successful",
                "newBalance", newBalance
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/change-password/{username}")
    public ResponseEntity<?> changePassword(
            @PathVariable String username,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(username, request);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/account/exists/{accountNumber}")
    public ResponseEntity<?> checkAccountExists(@PathVariable String accountNumber) {
        boolean exists = userService.accountExists(accountNumber);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}