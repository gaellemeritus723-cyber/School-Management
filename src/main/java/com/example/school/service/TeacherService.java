package com.example.school.service;

import com.example.school.model.*;
import com.example.school.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepo;
    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(TeacherRepository teacherRepo, AppUserRepository userRepo,
                          PasswordEncoder passwordEncoder) {
        this.teacherRepo = teacherRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Teacher createTeacher(Teacher teacher) {
        // Créer le compte AppUser : login = teacherCode, password = nom complet
        AppUser user = new AppUser();
        user.setUsername(teacher.getTeacherCode());
        user.setPassword(passwordEncoder.encode(teacher.getName()));
        user.setRole(Role.TEACHER);
        user.setDisplayName(teacher.getName());

        teacher.setAppUser(user);
        return teacherRepo.save(teacher);
    }

    @Transactional
    public Teacher updateTeacher(Teacher teacher) {
        Teacher existing = teacherRepo.findById(teacher.getId()).orElseThrow();

        AppUser user = existing.getAppUser();
        if (user != null) {
            user.setUsername(teacher.getTeacherCode());
            user.setPassword(passwordEncoder.encode(teacher.getName()));
            user.setDisplayName(teacher.getName());
            teacher.setAppUser(user);
        } else {
            AppUser newUser = new AppUser();
            newUser.setUsername(teacher.getTeacherCode());
            newUser.setPassword(passwordEncoder.encode(teacher.getName()));
            newUser.setRole(Role.TEACHER);
            newUser.setDisplayName(teacher.getName());
            teacher.setAppUser(newUser);
        }
        return teacherRepo.save(teacher);
    }
}
