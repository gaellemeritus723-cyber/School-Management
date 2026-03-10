package com.example.school.repository;

import com.example.school.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findByNameContainingIgnoreCase(String name);
    Optional<Teacher> findByTeacherCode(String teacherCode);
    Optional<Teacher> findByAppUser_Username(String username);
}
