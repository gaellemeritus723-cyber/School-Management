package com.example.school.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String message = (String) request.getAttribute("jakarta.servlet.error.message");

        model.addAttribute("status", statusCode != null ? statusCode : 500);
        model.addAttribute("message", message != null ? message : "Une erreur inattendue s'est produite.");
        model.addAttribute("is404", HttpStatus.NOT_FOUND.value() == (statusCode != null ? statusCode : 0));
        return "error";
    }
}
