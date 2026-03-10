package com.example.school.controller;

import com.example.school.model.Teacher;
import com.example.school.repository.TeacherRepository;
import com.example.school.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherRepository repo;
    private final TeacherService teacherService;

    public TeacherController(TeacherRepository repo, TeacherService teacherService) {
        this.repo = repo;
        this.teacherService = teacherService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("teachers", search != null && !search.isBlank()
                ? repo.findByNameContainingIgnoreCase(search)
                : repo.findAll());
        model.addAttribute("search", search);
        return "teachers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("action", "Ajouter");
        return "teachers/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute Teacher teacher, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("action", "Ajouter");
            return "teachers/form";
        }
        if (repo.findByTeacherCode(teacher.getTeacherCode()).isPresent()) {
            result.rejectValue("teacherCode", "duplicate", "Ce code enseignant est déjà utilisé");
            model.addAttribute("action", "Ajouter");
            return "teachers/form";
        }
        teacherService.createTeacher(teacher);
        ra.addFlashAttribute("success",
            "Enseignant ajouté ! Login: " + teacher.getTeacherCode() + " | Mot de passe: " + teacher.getName());
        return "redirect:/teachers";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", repo.findById(id).orElseThrow());
        model.addAttribute("action", "Modifier");
        return "teachers/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Teacher teacher,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("action", "Modifier");
            return "teachers/form";
        }
        teacher.setId(id);
        teacherService.updateTeacher(teacher);
        ra.addFlashAttribute("success", "Enseignant modifié avec succès !");
        return "redirect:/teachers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("success", "Enseignant supprimé.");
        return "redirect:/teachers";
    }
}
