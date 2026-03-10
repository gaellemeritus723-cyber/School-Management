package com.example.school.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "grades")
public class Grade {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    // Session 1 ou 2
    @NotNull(message = "La session est obligatoire")
    private Integer session; // 1 ou 2

    // Type : INTRA ou FINAL
    @NotBlank(message = "Le type d'examen est obligatoire")
    private String examType; // "INTRA" ou "FINAL"

    // Note sur 100
    @NotNull(message = "La note est obligatoire")
    @Min(value = 0, message = "La note ne peut pas être négative")
    @Max(value = 100, message = "La note ne peut pas dépasser 100")
    private Double score;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Integer getSession() { return session; }
    public void setSession(Integer session) { this.session = session; }
    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
}
