package com.loanmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loanmanagement.entity.LoanApplication;
import com.loanmanagement.entity.LoanStatus;
import com.loanmanagement.entity.User;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    List<LoanApplication> findByCustomerOrderBySubmittedAtDesc(User customer);

    List<LoanApplication> findByAssignedAdminOrderBySubmittedAtDesc(User assignedAdmin);

    List<LoanApplication> findAllByOrderBySubmittedAtDesc();

    List<LoanApplication> findByStatusOrderBySubmittedAtDesc(LoanStatus status);
}