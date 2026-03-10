package com.example.school.service;

import com.example.school.model.*;
import com.example.school.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepo;
    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepo, AppUserRepository userRepo,
                          PasswordEncoder passwordEncoder) {
        this.studentRepo = studentRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Student createStudent(Student student) {
        // Créer le compte AppUser : login = studentCode, password = nom complet
        AppUser user = new AppUser();
        user.setUsername(student.getStudentCode());
        user.setPassword(passwordEncoder.encode(student.getName()));
        user.setRole(Role.STUDENT);
        user.setDisplayName(student.getName());

        student.setAppUser(user);
        return studentRepo.save(student);
    }

    @Transactional
    public Student updateStudent(Student student) {
        Student existing = studentRepo.findById(student.getId()).orElseThrow();

        // Mettre à jour le AppUser existant si le code ou le nom change
        AppUser user = existing.getAppUser();
        if (user != null) {
            user.setUsername(student.getStudentCode());
            user.setPassword(passwordEncoder.encode(student.getName()));
            user.setDisplayName(student.getName());
            student.setAppUser(user);
        } else {
            // Créer un nouveau compte s'il n'en avait pas
            AppUser newUser = new AppUser();
            newUser.setUsername(student.getStudentCode());
            newUser.setPassword(passwordEncoder.encode(student.getName()));
            newUser.setRole(Role.STUDENT);
            newUser.setDisplayName(student.getName());
            student.setAppUser(newUser);
        }
        return studentRepo.save(student);
    }
}
