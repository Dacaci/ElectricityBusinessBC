package com.eb.frontend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
        // Supprimer le cookie JWT (même nom, maxAge=0)
        ResponseCookie jwtCookie = ResponseCookie.from("JWT_TOKEN", "")
            .httpOnly(true)
            .secure(request.isSecure())  // Dynamique : true en HTTPS, false en HTTP
            .path("/")
            .maxAge(0)           // Expire immédiatement
            .sameSite("Lax")
            .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        
        // Appeler aussi l'API de logout pour être sûr
        // (le cookie sera supprimé par le header ci-dessus)
        
        // Rediriger vers /login
        return "redirect:/login";
    }
}

