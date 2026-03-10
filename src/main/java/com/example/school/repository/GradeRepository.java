package com.example.school.repository;

import com.example.school.model.Grade;
import com.example.school.model.Student;
import com.example.school.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Toutes les notes d'un étudiant
    List<Grade> findByStudent(Student student);

    // Notes d'un étudiant pour une session donnée
    List<Grade> findByStudentAndSession(Student student, Integer session);

    // Notes d'un cours donné (pour le prof)
    List<Grade> findByCourse(Course course);

    // Notes d'un cours pour une session et type d'examen
    List<Grade> findByCourseAndSessionAndExamType(Course course, Integer session, String examType);

    // Vérifier si une note existe déjà pour éviter les doublons
    Optional<Grade> findByStudentAndCourseAndSessionAndExamType(
        Student student, Course course, Integer session, String examType);

    // Toutes les notes d'un étudiant pour un cours
    List<Grade> findByStudentAndCourse(Student student, Course course);
}
