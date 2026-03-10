package com.example.school.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le code enseignant est obligatoire")
    @Column(unique = true)
    private String teacherCode;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "La matière est obligatoire")
    private String subject;

    private String email;
    private String phone;

    // Lien vers le compte de connexion
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTeacherCode() { return teacherCode; }
    public void setTeacherCode(String teacherCode) { this.teacherCode = teacherCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public AppUser getAppUser() { return appUser; }
    public void setAppUser(AppUser appUser) { this.appUser = appUser; }
}
