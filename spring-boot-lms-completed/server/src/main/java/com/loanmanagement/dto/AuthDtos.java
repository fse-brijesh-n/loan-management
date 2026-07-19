package com.loanmanagement.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.loanmanagement.entity.LoanStatus;
import com.loanmanagement.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank String fullName,
            @Email @NotBlank String email,
            @Size(min = 8, message = "Password must be at least 8 characters") String password,
            @NotNull Role role,
            String organizationName) {
    }

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotNull Role role) {
    }

    public record AuthResponse(
            String token,
            String tokenType,
            Long userId,
            String fullName,
            String email,
            Role role,
            String organizationName) {
    }

    public record LoanApplicationRequest(
            @NotNull @Positive BigDecimal amount,
            @NotNull @Positive Integer tenureMonths,
            @NotBlank @Size(max = 1000) String purpose,
            @NotNull Long assignedAdminId) {
    }

    public record LoanDecisionRequest(
            @Size(max = 1000) String reason) {
    }

    public record LoanResponse(
            Long id,
            Long customerId,
            String customerName,
            String customerEmail,
            Long assignedAdminId,
            String assignedAdminName,
            String assignedAdminOrganizationName,
            BigDecimal amount,
            Integer tenureMonths,
            String purpose,
            LoanStatus status,
            String remarks,
            String decisionReason,
            Instant submittedAt,
            Instant decidedAt,
            Long reviewedById,
            String reviewedByName) {
    }

    public record AdminSummary(
            Long id,
            String fullName,
            String email,
            String organizationName) {
    }
}