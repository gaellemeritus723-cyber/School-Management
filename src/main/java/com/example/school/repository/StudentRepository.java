package com.example.school.repository;

import com.example.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByNameContainingIgnoreCase(String name);
    Optional<Student> findByStudentCode(String studentCode);
    Optional<Student> findByAppUser_Username(String username);
}
