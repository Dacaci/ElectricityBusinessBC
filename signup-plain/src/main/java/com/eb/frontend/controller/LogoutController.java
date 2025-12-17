package com.eb.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // La déconnexion JWT est gérée côté client (auth.js)
        // Ce contrôleur redirige simplement vers la carte
        // Le JavaScript se charge de nettoyer localStorage et d'appeler l'API /api/auth/logout
        return "redirect:/map";
    }
}

