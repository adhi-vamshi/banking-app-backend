package com.bankingApp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankingApp.model.LoanApplication;
import com.bankingApp.model.LoanApplicationRequest;
import com.bankingApp.model.LoanStatus;
import com.bankingApp.repository.LoanRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LoanService {
    
    @Autowired
    private LoanRepository loanRepository;
    
    // Generate random OTP
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    public LoanApplication applyForLoan(String username, String accountNumber, LoanApplicationRequest request) throws Exception {
        // Check if PAN or Aadhaar already exists
        if (loanRepository.existsByPanNumber(request.getPanNumber())) {
            throw new Exception("PAN number already exists in our system");
        }
        
        if (loanRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
            throw new Exception("Aadhaar number already exists in our system");
        }
        
        // Generate OTP
        String otp = generateOtp();
        
        // Create loan application
        LoanApplication loanApplication = new LoanApplication(
            username,
            accountNumber,
            request.getFullName(),
            request.getPanNumber(),
            request.getAadhaarNumber(),
            request.getDateOfBirth(),
            request.getAddress(),
            request.getMobileNumber(),
            request.getAge(),
            request.getMaritalStatus(),
            request.getGender(),
            request.getLoanAmount(),
            request.getLoanType(),
            request.getEmploymentType(),
            request.getMonthlyIncome()
        );
        
        loanApplication.setOtp(otp);
        loanApplication.setOtpVerified(false);
        loanApplication.setLoanStatus(LoanStatus.PENDING);
        
        // In a real application, you would send OTP via SMS here
        System.out.println("OTP for mobile " + request.getMobileNumber() + ": " + otp);
        
        return loanRepository.save(loanApplication);
    }
    
    public boolean verifyOtp(String mobileNumber, String otp) throws Exception {
        // In a real application, you would find by mobile number
        // For demo, we'll find the most recent application with this mobile
        List<LoanApplication> applications = loanRepository.findAll();
        Optional<LoanApplication> loanApp = applications.stream()
            .filter(app -> app.getMobileNumber().equals(mobileNumber) && !app.getOtpVerified())
            .findFirst();
            
        if (loanApp.isEmpty()) {
            throw new Exception("No pending loan application found for this mobile number");
        }
        
        LoanApplication application = loanApp.get();
        
        if (!application.getOtp().equals(otp)) {
            throw new Exception("Invalid OTP");
        }
        
        application.setOtpVerified(true);
        application.setVerifiedDate(LocalDateTime.now());
        loanRepository.save(application);
        
        return true;
    }
    
    public List<LoanApplication> getUserLoans(String username) {
        return loanRepository.findByUsername(username);
    }
    
    public List<LoanApplication> getAllLoans() {
        return loanRepository.findAll();
    }
    
    public LoanApplication updateLoanStatus(Long loanId, LoanStatus status) throws Exception {
        Optional<LoanApplication> loanOpt = loanRepository.findById(loanId);
        if (loanOpt.isEmpty()) {
            throw new Exception("Loan application not found");
        }
        
        LoanApplication loan = loanOpt.get();
        loan.setLoanStatus(status);
        
        return loanRepository.save(loan);
    }
    
    public Optional<LoanApplication> getLoanById(Long loanId) {
        return loanRepository.findById(loanId);
    }
}

