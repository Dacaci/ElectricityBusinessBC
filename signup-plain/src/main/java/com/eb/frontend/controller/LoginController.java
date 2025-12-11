package com.eb.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// DÉSACTIVÉ : LoginServlet SANS framework est utilisé à la place pour cohérence
// @Controller
public class LoginController {

    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String email,
            Model model) {
        model.addAttribute("backendUrl", backendUrl);
        if (message != null) {
            model.addAttribute("message", message);
        }
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "login";
    }
}

