package com.eb.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LocationsController {

    // IMPORTANT: En production sur Render, backend.url doit être en HTTPS via BACKEND_URL
    @Value("${backend.url:https://localhost:8080}")
    private String backendUrl;

    @GetMapping("/locations")
    public String locations(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        return "locations";
    }

    @GetMapping("/add-location")
    public String addLocation(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        return "add-location";
    }

    @GetMapping("/edit-location")
    public String editLocation(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        return "edit-location";
    }
}

