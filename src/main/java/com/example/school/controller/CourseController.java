package com.example.school.controller;

import com.example.school.model.Course;
import com.example.school.repository.CourseRepository;
import com.example.school.repository.TeacherRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepo;
    private final TeacherRepository teacherRepo;

    public CourseController(CourseRepository courseRepo, TeacherRepository teacherRepo) {
        this.courseRepo = courseRepo; this.teacherRepo = teacherRepo;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("courses", search != null && !search.isBlank()
                ? courseRepo.findByTitleContainingIgnoreCase(search)
                : courseRepo.findAll());
        model.addAttribute("search", search);
        return "courses/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("teachers", teacherRepo.findAll());
        model.addAttribute("action", "Ajouter");
        return "courses/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute Course course, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("teachers", teacherRepo.findAll());
            model.addAttribute("action", "Ajouter");
            return "courses/form";
        }
        courseRepo.save(course);
        ra.addFlashAttribute("success", "Cours ajouté avec succès !");
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseRepo.findById(id).orElseThrow());
        model.addAttribute("teachers", teacherRepo.findAll());
        model.addAttribute("action", "Modifier");
        return "courses/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Course course,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("teachers", teacherRepo.findAll());
            model.addAttribute("action", "Modifier");
            return "courses/form";
        }
        course.setId(id);
        courseRepo.save(course);
        ra.addFlashAttribute("success", "Cours modifié avec succès !");
        return "redirect:/courses";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        courseRepo.deleteById(id);
        ra.addFlashAttribute("success", "Cours supprimé.");
        return "redirect:/courses";
    }
}
