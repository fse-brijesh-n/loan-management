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
import com.loanmanagement.entity.User;
import com.loanmanagement.repository.LoanApplicationRepository;

@Service
public class LoanService {

    private final LoanApplicationRepository loanRepository;

    public LoanService(LoanApplicationRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Transactional
    public LoanResponse apply(User customer, LoanApplicationRequest request) {
        LoanApplication application = new LoanApplication();
        application.setCustomer(customer);
        application.setAmount(request.amount());
        application.setTenureMonths(request.tenureMonths());
        application.setPurpose(request.purpose());
        application.setStatus(LoanStatus.PENDING);
        return toResponse(loanRepository.save(application));
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansForUser(User user) {
        if (user.getRole() == null || user.getRole().name().equals("ADMIN")) {
            return loanRepository.findAllByOrderBySubmittedAtDesc().stream().map(this::toResponse).toList();
        }
        return loanRepository.findByCustomerOrderBySubmittedAtDesc(user).stream().map(this::toResponse).toList();
    }

    @Transactional
    public LoanResponse approve(Long loanId, User admin, LoanDecisionRequest request) {
        LoanApplication application = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found"));
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