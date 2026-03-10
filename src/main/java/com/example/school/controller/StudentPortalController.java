package com.example.school.controller;

import com.example.school.model.Student;
import com.example.school.repository.StudentRepository;
import com.example.school.service.GradeService;
import com.example.school.service.GradeService.BulletinData;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentPortalController {

    private final StudentRepository studentRepo;
    private final GradeService gradeService;

    public StudentPortalController(StudentRepository studentRepo, GradeService gradeService) {
        this.studentRepo = studentRepo;
        this.gradeService = gradeService;
    }

    @GetMapping("/bulletin")
    public String bulletin(Authentication auth,
                           @RequestParam(required = false) Integer session,
                           Model model) {
        Student student = studentRepo.findByAppUser_Username(auth.getName()).orElseThrow();

        BulletinData bulletin = (session != null)
                ? gradeService.getBulletinDataBySession(student, session)
                : gradeService.getBulletinData(student);

        model.addAttribute("student", student);
        model.addAttribute("bulletin", bulletin);
        model.addAttribute("selectedSession", session);
        return "student/bulletin";
    }
}
