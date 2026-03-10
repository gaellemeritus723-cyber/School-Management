package com.example.school.config;

import com.example.school.model.Teacher;
import com.example.school.repository.TeacherRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TeacherConverter implements Converter<String, Teacher> {

    private final TeacherRepository teacherRepo;

    public TeacherConverter(TeacherRepository teacherRepo) {
        this.teacherRepo = teacherRepo;
    }

    @Override
    public Teacher convert(String id) {
        if (id == null || id.isBlank()) return null;
        return teacherRepo.findById(Long.parseLong(id)).orElse(null);
    }
}