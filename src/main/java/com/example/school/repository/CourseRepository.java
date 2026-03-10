package com.example.school.repository;

import com.example.school.model.Course;
import com.example.school.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTitleContainingIgnoreCase(String title);
    List<Course> findByTeacher(Teacher teacher);
}
