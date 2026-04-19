package com.bankingApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankingApp.model.LoanApplication;

@Repository
public interface LoanRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByUsername(String username);
    Optional<LoanApplication> findByPanNumber(String panNumber);
    Optional<LoanApplication> findByAadhaarNumber(String aadhaarNumber);
    boolean existsByPanNumber(String panNumber);
    boolean existsByAadhaarNumber(String aadhaarNumber);
    List<LoanApplication> findByLoanStatus(String loanStatus);
}