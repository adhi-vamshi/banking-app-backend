package com.bankingApp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankingApp.model.LoanApplication;
import com.bankingApp.model.LoanApplicationRequest;
import com.bankingApp.model.LoanStatus;
import com.bankingApp.model.OtpVerificationRequest;
import com.bankingApp.service.LoanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loan")
@CrossOrigin(origins = "http://localhost:3000")
public class LoanController {
    
    @Autowired
    private LoanService loanService;
    
    @PostMapping("/apply")
    public ResponseEntity<?> applyForLoan(
            @RequestHeader("username") String username,
            @RequestHeader("accountNumber") String accountNumber,
            @Valid @RequestBody LoanApplicationRequest request) {
        try {
            LoanApplication loanApplication = loanService.applyForLoan(username, accountNumber, request);
            return ResponseEntity.ok(Map.of(
                "message", "Loan application submitted successfully. OTP sent to your mobile.",
                "applicationId", loanApplication.getId(),
                "otp", loanApplication.getOtp() // In production, remove this line and send via SMS
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        try {
            boolean verified = loanService.verifyOtp(request.getMobileNumber(), request.getOtp());
            return ResponseEntity.ok(Map.of(
                "message", "OTP verified successfully",
                "verified", verified
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/my-loans")
    public ResponseEntity<?> getMyLoans(@RequestHeader("username") String username) {
        try {
            List<LoanApplication> loans = loanService.getUserLoans(username);
            return ResponseEntity.ok(Map.of("loans", loans));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllLoans() {
        try {
            List<LoanApplication> loans = loanService.getAllLoans();
            return ResponseEntity.ok(Map.of("loans", loans));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{loanId}/status")
    public ResponseEntity<?> updateLoanStatus(
            @PathVariable Long loanId,
            @RequestParam LoanStatus status) {
        try {
            LoanApplication loan = loanService.updateLoanStatus(loanId, status);
            return ResponseEntity.ok(Map.of(
                "message", "Loan status updated successfully",
                "loan", loan
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{loanId}")
    public ResponseEntity<?> getLoanById(@PathVariable Long loanId) {
        try {
            return loanService.getLoanById(loanId)
                    .map(loan -> ResponseEntity.ok(Map.of("loan", loan)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        return ResponseEntity.ok(Map.of("message", "Loan API is working!"));
    }
}
