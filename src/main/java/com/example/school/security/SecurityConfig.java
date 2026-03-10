package com.example.school.security;

import com.example.school.service.AppUserDetailsService;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;

    public SecurityConfig(AppUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication auth) -> {
            String role = auth.getAuthorities().iterator().next().getAuthority();
            String redirect;
            switch (role) {
                case "ROLE_ADMIN":   redirect = "/dashboard"; break;
                case "ROLE_TEACHER": redirect = "/teacher/home"; break;
                case "ROLE_STUDENT": redirect = "/student/bulletin"; break;
                default:             redirect = "/dashboard";
            }
            response.sendRedirect(redirect);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/error", "/css/**", "/js/**", "/favicon.ico").permitAll()
                // ADMIN uniquement
                .requestMatchers("/students/new", "/students/edit/**", "/students/delete/**").hasRole("ADMIN")
                .requestMatchers("/teachers/new", "/teachers/edit/**", "/teachers/delete/**").hasRole("ADMIN")
                .requestMatchers("/courses/new", "/courses/edit/**", "/courses/delete/**").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/report/pdf").hasRole("ADMIN")
                // Pages étudiant
                .requestMatchers("/student/**").hasRole("STUDENT")
                // Pages enseignant
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                // Tout authentifié peut consulter
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(roleBasedSuccessHandler())
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}
