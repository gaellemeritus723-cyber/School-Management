package com.example.school.service;

import com.example.school.model.*;
import com.example.school.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GradeService {

    private final GradeRepository gradeRepo;
    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;

    public GradeService(GradeRepository gradeRepo, StudentRepository studentRepo,
                        CourseRepository courseRepo) {
        this.gradeRepo = gradeRepo;
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
    }

    // Sauvegarder ou mettre à jour une note
    public Grade saveOrUpdate(Grade grade) {
        Optional<Grade> existing = gradeRepo.findByStudentAndCourseAndSessionAndExamType(
                grade.getStudent(), grade.getCourse(),
                grade.getSession(), grade.getExamType());
        existing.ifPresent(g -> grade.setId(g.getId()));
        return gradeRepo.save(grade);
    }

    // Récupérer toutes les notes d'un étudiant groupées par cours et session
    public BulletinData getBulletinData(Student student) {
        List<Grade> allGrades = gradeRepo.findByStudent(student);
        return buildBulletin(student, allGrades);
    }

    // Récupérer les notes d'un étudiant pour une session spécifique
    public BulletinData getBulletinDataBySession(Student student, Integer session) {
        List<Grade> grades = gradeRepo.findByStudentAndSession(student, session);
        return buildBulletin(student, grades);
    }

    private BulletinData buildBulletin(Student student, List<Grade> grades) {
        // Grouper par cours
        Map<Course, Map<String, Map<Integer, Double>>> structure = new LinkedHashMap<>();

        for (Grade g : grades) {
            structure
                .computeIfAbsent(g.getCourse(), c -> new LinkedHashMap<>())
                .computeIfAbsent(g.getExamType(), t -> new LinkedHashMap<>())
                .put(g.getSession(), g.getScore());
        }

        // Construire les lignes du bulletin
        List<BulletinRow> rows = new ArrayList<>();
        List<String> reprises = new ArrayList<>();

        for (Map.Entry<Course, Map<String, Map<Integer, Double>>> entry : structure.entrySet()) {
            Course course = entry.getKey();
            Map<String, Map<Integer, Double>> byType = entry.getValue();

            // Calculer la moyenne de la matière (moy = (intra + final) / 2 par session)
            // On prend toutes les notes de la matière et on fait la moyenne
            List<Double> allScores = grades.stream()
                .filter(g -> g.getCourse().equals(course))
                .map(Grade::getScore)
                .collect(Collectors.toList());

            double courseMoyenne = allScores.isEmpty() ? 0 :
                allScores.stream().mapToDouble(Double::doubleValue).average().orElse(0);

            BulletinRow row = new BulletinRow();
            row.courseName = course.getTitle();
            row.gradesByTypeAndSession = byType;
            row.moyenne = Math.round(courseMoyenne * 100.0) / 100.0;
            rows.add(row);

            if (courseMoyenne < 65) {
                reprises.add(course.getTitle());
            }
        }

        // Moyenne générale = somme de toutes les notes / nombre de notes
        double moyenneGenerale = grades.isEmpty() ? 0 :
            grades.stream().mapToDouble(Grade::getScore).average().orElse(0);
        moyenneGenerale = Math.round(moyenneGenerale * 100.0) / 100.0;

        BulletinData data = new BulletinData();
        data.student = student;
        data.rows = rows;
        data.reprises = reprises;
        data.moyenneGenerale = moyenneGenerale;
        return data;
    }

    // ─── DTOs internes ────────────────────────────────────────────────────────

    public static class BulletinData {
        public Student student;
        public List<BulletinRow> rows = new ArrayList<>();
        public List<String> reprises = new ArrayList<>();
        public double moyenneGenerale;
    }

    public static class BulletinRow {
        public String courseName;
        public Map<String, Map<Integer, Double>> gradesByTypeAndSession; // examType -> session -> score
        public double moyenne;

        // Helper pour Thymeleaf
        public Double getScore(String examType, Integer session) {
            if (gradesByTypeAndSession == null) return null;
            Map<Integer, Double> bySession = gradesByTypeAndSession.get(examType);
            if (bySession == null) return null;
            return bySession.get(session);
        }
    }
}
