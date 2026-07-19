package com.loanmanagement.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loanmanagement.dto.AuthDtos.AdminSummary;
import com.loanmanagement.entity.Role;
import com.loanmanagement.repository.UserRepository;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<AdminSummary> getAdmins() {
        return userRepository.findByRole(Role.ADMIN).stream()
                .map(admin -> new AdminSummary(admin.getId(), admin.getFullName(), admin.getEmail(), admin.getOrganizationName()))
                .toList();
    }
}