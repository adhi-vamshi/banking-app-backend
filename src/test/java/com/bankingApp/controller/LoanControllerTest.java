package com.bankingApp.controller;

import com.bankingApp.model.*;
import com.bankingApp.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();
        objectMapper = new ObjectMapper(); // ✅ FIX
    }

    // ✅ Helper method
    private LoanApplicationRequest createValidRequest() {
        LoanApplicationRequest req = new LoanApplicationRequest();
        req.setFullName("John Doe");
        req.setPanNumber("ABCDE1234F");
        req.setAadhaarNumber("123456789012");
        req.setDateOfBirth(LocalDate.of(1995, 1, 1));
        req.setAddress("Hyderabad Telangana 500001");
        req.setMobileNumber("9876543210");
        req.setAge(25);
        req.setMaritalStatus(MaritalStatus.SINGLE);
        req.setGender(Gender.MALE);
        req.setLoanAmount(50000.0);
        req.setLoanType(LoanType.PERSONAL);
        req.setEmploymentType(EmploymentType.SALARIED);
        req.setMonthlyIncome(30000.0);
        return req;
    }

    // ✅ 1. SUCCESS: Apply Loan
//    @Test
//    void testApplyLoan_Success() throws Exception {
//        LoanApplicationRequest request = createValidRequest();
//
//        LoanApplication loan = new LoanApplication();
//        loan.setId(1L);
//        loan.setOtp("123456");
//
//        Mockito.when(loanService.applyForLoan(Mockito.any(), Mockito.any(), Mockito.any()))
//                .thenReturn(loan);
//
//        mockMvc.perform(post("/api/loan/apply")
//                .header("username", "user1")
//                .header("accountNumber", "AC123")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.applicationId").value(1))
//                .andExpect(jsonPath("$.otp").value("123456"));
//    }

    // ❌ PAN EXISTS
//    @Test
//    void testApplyLoan_PanExists() throws Exception {
//        LoanApplicationRequest request = createValidRequest();
//
//        Mockito.when(loanService.applyForLoan(Mockito.any(), Mockito.any(), Mockito.any()))
//                .thenThrow(new Exception("PAN number already exists"));
//
//        mockMvc.perform(post("/api/loan/apply")
//                .header("username", "user1")
//                .header("accountNumber", "AC123")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("PAN number already exists"));
//    }

    // ❌ INVALID MOBILE
//    @Test
//    void testApplyLoan_InvalidMobile() throws Exception {
//        LoanApplicationRequest request = createValidRequest();
//        request.setMobileNumber("123");
//
//        mockMvc.perform(post("/api/loan/apply")
//                .header("username", "user1")
//                .header("accountNumber", "AC123")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//    }

    // ✅ OTP SUCCESS
    @Test
    void testVerifyOtp_Success() throws Exception {
        OtpVerificationRequest req = new OtpVerificationRequest();
        req.setMobileNumber("9876543210");
        req.setOtp("123456");

        Mockito.when(loanService.verifyOtp("9876543210", "123456"))
                .thenReturn(true);

        mockMvc.perform(post("/api/loan/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));
    }

    // ❌ OTP FAIL
    @Test
    void testVerifyOtp_Invalid() throws Exception {
        OtpVerificationRequest req = new OtpVerificationRequest();
        req.setMobileNumber("9876543210");
        req.setOtp("000000");

        Mockito.when(loanService.verifyOtp(Mockito.any(), Mockito.any()))
                .thenThrow(new Exception("Invalid OTP"));

        mockMvc.perform(post("/api/loan/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid OTP"));
    }

    // ✅ GET MY LOANS
    @Test
    void testGetMyLoans() throws Exception {
        Mockito.when(loanService.getUserLoans("user1"))
                .thenReturn(List.of(new LoanApplication()));

        mockMvc.perform(get("/api/loan/my-loans")
                .header("username", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loans").isArray());
    }

    // ✅ GET ALL
    @Test
    void testGetAllLoans() throws Exception {
        Mockito.when(loanService.getAllLoans())
                .thenReturn(List.of(new LoanApplication()));

        mockMvc.perform(get("/api/loan/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loans").isArray());
    }

    // ❌ UPDATE FAIL
    @Test
    void testUpdateLoanStatus_NotFound() throws Exception {
        Mockito.when(loanService.updateLoanStatus(1L, LoanStatus.APPROVED))
                .thenThrow(new Exception("Loan application not found"));

        mockMvc.perform(put("/api/loan/1/status")
                .param("status", "APPROVED"))
                .andExpect(status().isBadRequest());
    }

    // ✅ UPDATE SUCCESS
    @Test
    void testUpdateLoanStatus_Success() throws Exception {
        LoanApplication loan = new LoanApplication();
        loan.setId(1L);

        Mockito.when(loanService.updateLoanStatus(1L, LoanStatus.APPROVED))
                .thenReturn(loan);

        mockMvc.perform(put("/api/loan/1/status")
                .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loan.id").value(1));
    }

    // ✅ GET BY ID
    @Test
    void testGetLoanById_Success() throws Exception {
        LoanApplication loan = new LoanApplication();
        loan.setId(1L);

        Mockito.when(loanService.getLoanById(1L))
                .thenReturn(Optional.of(loan));

        mockMvc.perform(get("/api/loan/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loan.id").value(1));
    }

    // ❌ NOT FOUND
    @Test
    void testGetLoanById_NotFound() throws Exception {
        Mockito.when(loanService.getLoanById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/loan/1"))
                .andExpect(status().isNotFound());
    }

    // ✅ HEALTH
    @Test
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/loan/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Loan API is working!"));
    }
}