package com.bankingApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankingApp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailId(String emailId);
    Optional<User> findByAccountNumber(String accountNumber);
    boolean existsByUsername(String username);
    boolean existsByEmailId(String emailId);
    boolean existsByAccountNumber(String accountNumber);
}