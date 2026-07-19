package com.loanmanagement.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loanmanagement.dto.AuthDtos.LoanApplicationRequest;
import com.loanmanagement.dto.AuthDtos.LoanDecisionRequest;
import com.loanmanagement.dto.AuthDtos.LoanResponse;
import com.loanmanagement.entity.LoanApplication;
import com.loanmanagement.entity.LoanStatus;
import com.loanmanagement.entity.Role;
import com.loanmanagement.entity.User;
import com.loanmanagement.repository.LoanApplicationRepository;
import com.loanmanagement.repository.UserRepository;

@Service
@SuppressWarnings("null")
public class LoanService {

    private final LoanApplicationRepository loanRepository;
    private final UserRepository userRepository;

    public LoanService(LoanApplicationRepository loanRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LoanResponse apply(User customer, LoanApplicationRequest request) {
        User assignedAdmin = userRepository.findById(request.assignedAdminId())
                .orElseThrow(() -> new IllegalArgumentException("Assigned admin not found"));
        if (assignedAdmin.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Assigned user must be an admin");
        }

        LoanApplication application = new LoanApplication();
        application.setCustomer(customer);
        application.setAmount(request.amount());
        application.setTenureMonths(request.tenureMonths());
        application.setPurpose(request.purpose());
        application.setAssignedAdmin(assignedAdmin);
        application.setStatus(LoanStatus.PENDING);
        return toResponse(loanRepository.save(application));
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansForUser(User user) {
        if (user.getRole() == null || user.getRole().name().equals("ADMIN")) {
            return loanRepository.findByAssignedAdminOrderBySubmittedAtDesc(user).stream().map(this::toResponse).toList();
        }
        return loanRepository.findByCustomerOrderBySubmittedAtDesc(user).stream().map(this::toResponse).toList();
    }

    @Transactional
    public LoanResponse approve(Long loanId, User admin, LoanDecisionRequest request) {
        LoanApplication application = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found"));
        if (application.getAssignedAdmin() == null || !application.getAssignedAdmin().getId().equals(admin.getId())) {
            throw new IllegalArgumentException("Loan is not assigned to this admin");
        }
        if (application.getStatus() != LoanStatus.PENDING) {
            throw new IllegalArgumentException("Only pending applications can be reviewed");
        }
        application.setStatus(LoanStatus.APPROVED);
        application.setReviewedBy(admin);
        application.setDecisionReason(request == null ? null : request.reason());
        application.setDecidedAt(Instant.now());
        return toResponse(application);
    }

    @Transactional
    public LoanResponse reject(Long loanId, User admin, LoanDecisionRequest request) {
        LoanApplication application = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found"));
        if (application.getAssignedAdmin() == null || !application.getAssignedAdmin().getId().equals(admin.getId())) {
            throw new IllegalArgumentException("Loan is not assigned to this admin");
        }
        if (application.getStatus() != LoanStatus.PENDING) {
            throw new IllegalArgumentException("Only pending applications can be reviewed");
        }
        application.setStatus(LoanStatus.REJECTED);
        application.setReviewedBy(admin);
        application.setDecisionReason(request == null ? null : request.reason());
        application.setDecidedAt(Instant.now());
        return toResponse(application);
    }

    private LoanResponse toResponse(LoanApplication application) {
        return new LoanResponse(
                application.getId(),
                application.getCustomer().getId(),
                application.getCustomer().getFullName(),
                application.getCustomer().getEmail(),
                application.getAssignedAdmin() == null ? null : application.getAssignedAdmin().getId(),
                application.getAssignedAdmin() == null ? null : application.getAssignedAdmin().getFullName(),
                application.getAssignedAdmin() == null ? null : application.getAssignedAdmin().getOrganizationName(),
                application.getAmount(),
                application.getTenureMonths(),
                application.getPurpose(),
                application.getStatus(),
                application.getRemarks(),
                application.getDecisionReason(),
                application.getSubmittedAt(),
                application.getDecidedAt(),
                application.getReviewedBy() == null ? null : application.getReviewedBy().getId(),
                application.getReviewedBy() == null ? null : application.getReviewedBy().getFullName());
    }
}