package com.loanmanagement.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.loanmanagement.entity.Role;
import com.loanmanagement.entity.User;
import com.loanmanagement.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        userRepository.findByEmail("admin@loan.com").orElseGet(() -> {
            User admin = new User();
            admin.setFullName("System Admin");
            admin.setEmail("admin@loan.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);
            admin.setOrganizationName("Central Loans Office");
            return userRepository.save(admin);
        });
    }
}