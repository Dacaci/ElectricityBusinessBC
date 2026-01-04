package com.eb.frontend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) throws IOException {
        ResponseCookie jwtCookie = ResponseCookie.from("JWT_TOKEN", "")
            .httpOnly(true)
            .secure(true)  // Nécessaire pour SameSite=None en HTTPS
            .sameSite("None")  // Permet l'envoi cross-origin (frontend/backend sur domaines différents)
            .path("/")
            .maxAge(0)
            .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        
        return "redirect:/map";
    }
}

