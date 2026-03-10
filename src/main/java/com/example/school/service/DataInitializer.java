package com.example.school.service;

import com.example.school.model.AppUser;
import com.example.school.model.Role;
import com.example.school.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AppUserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepo.findByUsername("admin").isEmpty()) {
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setDisplayName("Administrateur");
            userRepo.save(admin);
            System.out.println("✅ Compte admin créé : login=admin / password=admin123");
        }
    }
}
