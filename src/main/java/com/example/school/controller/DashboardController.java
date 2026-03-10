package com.example.school.controller;

import com.example.school.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final CourseRepository courseRepo;

    public DashboardController(StudentRepository s, TeacherRepository t, CourseRepository c) {
        this.studentRepo = s; this.teacherRepo = t; this.courseRepo = c;
    }

    @GetMapping("/")
    public String root() { return "redirect:/dashboard"; }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("students", studentRepo.count());
        model.addAttribute("teachers", teacherRepo.count());
        model.addAttribute("courses", courseRepo.count());
        model.addAttribute("username", auth.getName());
        model.addAttribute("isAdmin", auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        return "dashboard";
    }

    @GetMapping("/access-denied")
    public String accessDenied() { return "access-denied"; }
}
