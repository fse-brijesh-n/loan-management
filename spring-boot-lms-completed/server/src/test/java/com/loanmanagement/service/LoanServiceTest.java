package com.loanmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.loanmanagement.dto.AuthDtos.LoanApplicationRequest;
import com.loanmanagement.dto.AuthDtos.LoanDecisionRequest;
import com.loanmanagement.dto.AuthDtos.LoanResponse;
import com.loanmanagement.entity.LoanApplication;
import com.loanmanagement.entity.LoanStatus;
import com.loanmanagement.entity.Role;
import com.loanmanagement.entity.User;
import com.loanmanagement.repository.LoanApplicationRepository;
import com.loanmanagement.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class LoanServiceTest {

    @Mock
    private LoanApplicationRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanService loanService;

    private User customer;
    private User admin;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(10L);
        customer.setFullName("Customer One");
        customer.setEmail("customer@example.com");
        customer.setRole(Role.CUSTOMER);

        admin = new User();
        admin.setId(20L);
        admin.setFullName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);
    }

    @Test
    void applyCreatesPendingLoan() {
        LoanApplicationRequest request = new LoanApplicationRequest(new BigDecimal("25000.00"), 24, "Home renovation", admin.getId());
        LoanApplication saved = buildLoan(1L, customer, LoanStatus.PENDING, request.amount(), request.tenureMonths(), request.purpose());
        when(loanRepository.save(org.mockito.ArgumentMatchers.<LoanApplication>any())).thenReturn(saved);
        when(userRepository.findById(admin.getId())).thenReturn(Optional.of(admin));

        LoanResponse response = loanService.apply(customer, request);

        assertEquals(LoanStatus.PENDING, response.status());
        assertEquals(customer.getId(), response.customerId());
        assertEquals(request.amount(), response.amount());
    }

    @Test
    void adminCanApprovePendingLoan() {
        LoanApplication existing = buildLoan(2L, customer, LoanStatus.PENDING, new BigDecimal("15000.00"), 18, "Car purchase");
        LoanDecisionRequest request = new LoanDecisionRequest("Meets criteria");
        when(loanRepository.findById(2L)).thenReturn(Optional.of(existing));

        LoanResponse response = loanService.approve(2L, admin, request);

        assertEquals(LoanStatus.APPROVED, response.status());
        assertEquals("Meets criteria", response.decisionReason());
        assertEquals(admin.getId(), response.reviewedById());
        verify(loanRepository).findById(2L);
    }

    @Test
    void nonPendingLoanCannotBeReviewedAgain() {
        LoanApplication existing = buildLoan(3L, customer, LoanStatus.APPROVED, new BigDecimal("15000.00"), 18, "Car purchase");
        when(loanRepository.findById(3L)).thenReturn(Optional.of(existing));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> loanService.reject(3L, admin, new LoanDecisionRequest("Already processed")));

        assertEquals("Only pending applications can be reviewed", exception.getMessage());
    }

    @Test
    void customerSeesOnlyOwnLoans() {
        LoanApplication loan = buildLoan(4L, customer, LoanStatus.PENDING, new BigDecimal("8000.00"), 12, "Equipment");
        when(loanRepository.findByCustomerOrderBySubmittedAtDesc(customer)).thenReturn(List.of(loan));

        List<LoanResponse> responses = loanService.getLoansForUser(customer);

        assertEquals(1, responses.size());
        assertEquals(LoanStatus.PENDING, responses.get(0).status());
    }

    @Test
    void adminSeesAllLoans() {
        LoanApplication loan = buildLoan(5L, customer, LoanStatus.REJECTED, new BigDecimal("5000.00"), 6, "Inventory");
        when(loanRepository.findByAssignedAdminOrderBySubmittedAtDesc(admin)).thenReturn(List.of(loan));

        List<LoanResponse> responses = loanService.getLoansForUser(admin);

        assertEquals(1, responses.size());
        assertEquals(LoanStatus.REJECTED, responses.get(0).status());
    }

    private LoanApplication buildLoan(Long id, User owner, LoanStatus status, BigDecimal amount, Integer tenure, String purpose) {
        LoanApplication application = new LoanApplication();
        application.setId(id);
        application.setCustomer(owner);
        application.setAssignedAdmin(admin);
        application.setStatus(status);
        application.setAmount(amount);
        application.setTenureMonths(tenure);
        application.setPurpose(purpose);
        application.setSubmittedAt(java.time.Instant.parse("2026-07-19T00:00:00Z"));
        return application;
    }
}