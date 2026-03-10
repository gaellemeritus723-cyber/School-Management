package com.example.school.controller;

import com.example.school.model.Teacher;
import com.example.school.repository.CourseRepository;
import com.example.school.repository.TeacherRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teacher")
public class TeacherPortalController {

    private final TeacherRepository teacherRepo;
    private final CourseRepository courseRepo;

    public TeacherPortalController(TeacherRepository teacherRepo, CourseRepository courseRepo) {
        this.teacherRepo = teacherRepo;
        this.courseRepo = courseRepo;
    }

    @GetMapping("/home")
    public String home(Authentication auth, Model model) {
        Teacher teacher = teacherRepo.findByAppUser_Username(auth.getName()).orElseThrow();
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courseRepo.findByTeacher(teacher));
        return "teacher/home";
    }
}
