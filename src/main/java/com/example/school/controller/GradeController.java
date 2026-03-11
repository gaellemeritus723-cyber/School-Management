package com.example.school.controller;

import com.example.school.model.*;
import com.example.school.repository.*;
import com.example.school.service.GradeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Enumeration;
import java.util.List;

@Controller
@RequestMapping("/teacher/grades")
public class GradeController {

    private final GradeRepository gradeRepo;
    private final GradeService gradeService;
    private final CourseRepository courseRepo;
    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;

    public GradeController(GradeRepository gradeRepo, GradeService gradeService,
                            CourseRepository courseRepo, StudentRepository studentRepo,
                            TeacherRepository teacherRepo) {
        this.gradeRepo = gradeRepo;
        this.gradeService = gradeService;
        this.courseRepo = courseRepo;
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
    }

    @GetMapping
    public String myCourses(Authentication auth, Model model) {
        Teacher teacher = getTeacher(auth);
        List<Course> courses = courseRepo.findByTeacher(teacher);
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses);
        return "teacher/grades/courses";
    }

    @GetMapping("/course/{courseId}")
    public String selectSession(@PathVariable Long courseId, Authentication auth, Model model) {
        Teacher teacher = getTeacher(auth);
        Course course = courseRepo.findById(courseId).orElseThrow();
        if (course.getTeacher() == null || !course.getTeacher().getId().equals(teacher.getId())) {
            return "redirect:/teacher/grades";
        }
        model.addAttribute("teacher", teacher);
        model.addAttribute("course", course);
        return "teacher/grades/select";
    }

    @GetMapping("/course/{courseId}/enter")
    public String enterGrades(@PathVariable Long courseId,
                              @RequestParam(name = "session") Integer sessionNum,
                              @RequestParam String examType,
                              Authentication auth, Model model) {
        Teacher teacher = getTeacher(auth);
        Course course = courseRepo.findById(courseId).orElseThrow();
        if (course.getTeacher() == null || !course.getTeacher().getId().equals(teacher.getId())) {
            return "redirect:/teacher/grades";
        }

        List<Student> students = studentRepo.findAll();
        List<GradeEntry> entries = students.stream().map(s -> {
            GradeEntry e = new GradeEntry();
            e.student = s;
            gradeRepo.findByStudentAndCourseAndSessionAndExamType(s, course, sessionNum, examType)
                     .ifPresent(g -> e.score = g.getScore());
            return e;
        }).toList();

        model.addAttribute("teacher", teacher);
        model.addAttribute("course", course);
        model.addAttribute("sessionNum", sessionNum);
        model.addAttribute("examType", examType);
        model.addAttribute("entries", entries);
        return "teacher/grades/enter";
    }

    // Tout lu via HttpServletRequest pour éviter le conflit avec "session" de Thymeleaf
    @PostMapping("/course/{courseId}/save")
    public String saveGrades(@PathVariable Long courseId,
                             HttpServletRequest request,
                             Authentication auth,
                             RedirectAttributes ra) {
        Teacher teacher = getTeacher(auth);
        Course course = courseRepo.findById(courseId).orElseThrow();
        if (course.getTeacher() == null || !course.getTeacher().getId().equals(teacher.getId())) {
            return "redirect:/teacher/grades";
        }

        Integer sessionNum = Integer.parseInt(request.getParameter("session"));
        String examType = request.getParameter("examType");

        int saved = 0;
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String key = paramNames.nextElement();
            if (!key.startsWith("score_")) continue;
            String value = request.getParameter(key);
            if (value == null || value.isBlank()) continue;
            Long studentId = Long.parseLong(key.replace("score_", ""));
            Student student = studentRepo.findById(studentId).orElse(null);
            if (student == null) continue;
            try {
                // Note enregistrée en Integer (arrondi)
                int scoreInt = (int) Math.round(Double.parseDouble(value));
                Grade grade = new Grade();
                grade.setStudent(student);
                grade.setCourse(course);
                grade.setSession(sessionNum);
                grade.setExamType(examType);
                grade.setScore((double) scoreInt);
                gradeService.saveOrUpdate(grade);
                saved++;
            } catch (NumberFormatException ignored) {}
        }

        ra.addFlashAttribute("success", saved + " note(s) enregistrée(s) avec succès !");
        return "redirect:/teacher/grades/course/" + courseId;
    }

    private Teacher getTeacher(Authentication auth) {
        return teacherRepo.findByAppUser_Username(auth.getName()).orElseThrow();
    }

    public static class GradeEntry {
        public Student student;
        public Double score;
    }
}
