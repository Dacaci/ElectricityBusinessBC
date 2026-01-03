package com.eb.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReservationsController {

    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;

    @GetMapping("/reservations")
    public String reservations(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        return "reservations";
    }

    @GetMapping("/add-reservation")
    public String addReservation(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        return "add-reservation";
    }
}

