package com.example.school.controller;

import com.example.school.model.Student;
import com.example.school.repository.StudentRepository;
import com.example.school.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository repo;
    private final StudentService studentService;

    public StudentController(StudentRepository repo, StudentService studentService) {
        this.repo = repo;
        this.studentService = studentService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("students", repo.findByNameContainingIgnoreCase(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("students", repo.findAll());
        }
        return "students/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("action", "Ajouter");
        return "students/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute Student student, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("action", "Ajouter");
            return "students/form";
        }
        // Vérifier si le code étudiant existe déjà
        if (repo.findByStudentCode(student.getStudentCode()).isPresent()) {
            result.rejectValue("studentCode", "duplicate", "Ce code étudiant est déjà utilisé");
            model.addAttribute("action", "Ajouter");
            return "students/form";
        }
        studentService.createStudent(student);
        ra.addFlashAttribute("success",
            "Étudiant ajouté ! Login: " + student.getStudentCode() + " | Mot de passe: " + student.getName());
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", repo.findById(id).orElseThrow());
        model.addAttribute("action", "Modifier");
        return "students/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Student student,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("action", "Modifier");
            return "students/form";
        }
        student.setId(id);
        studentService.updateStudent(student);
        ra.addFlashAttribute("success", "Étudiant modifié avec succès !");
        return "redirect:/students";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("success", "Étudiant supprimé.");
        return "redirect:/students";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("student", repo.findById(id).orElseThrow());
        return "students/view";
    }
}
