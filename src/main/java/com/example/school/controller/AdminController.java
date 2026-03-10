package com.example.school.controller;

import com.example.school.model.AppUser;
import com.example.school.model.Role;
import com.example.school.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AppUserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo; this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("newUser", new AppUser());
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute AppUser user,
                             @RequestParam String rawPassword,
                             RedirectAttributes ra) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepo.save(user);
        ra.addFlashAttribute("success", "Utilisateur créé !");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userRepo.deleteById(id);
        ra.addFlashAttribute("success", "Utilisateur supprimé.");
        return "redirect:/admin/users";
    }
}
