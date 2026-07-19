package com.loanmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loanmanagement.dto.AuthDtos.LoanApplicationRequest;
import com.loanmanagement.dto.AuthDtos.LoanDecisionRequest;
import com.loanmanagement.dto.AuthDtos.LoanResponse;
import com.loanmanagement.entity.User;
import com.loanmanagement.service.LoanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<LoanResponse> apply(@AuthenticationPrincipal User user,
            @Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity.ok(loanService.apply(user, request));
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(loanService.getLoansForUser(user));
    }

    @PutMapping("/{loanId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponse> approve(@PathVariable Long loanId,
            @AuthenticationPrincipal User user,
            @RequestBody(required = false) LoanDecisionRequest request) {
        return ResponseEntity.ok(loanService.approve(loanId, user, request));
    }

    @PutMapping("/{loanId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponse> reject(@PathVariable Long loanId,
            @AuthenticationPrincipal User user,
            @RequestBody(required = false) LoanDecisionRequest request) {
        return ResponseEntity.ok(loanService.reject(loanId, user, request));
    }
}

