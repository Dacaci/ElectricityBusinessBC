package com.eb.frontend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StationsController {

    // IMPORTANT: En production sur Render, backend.url doit être en HTTPS via BACKEND_URL
    @Value("${backend.url:https://localhost:8080}")
    private String backendUrl;

    @GetMapping("/stations")
    public String stations(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        return "stations";
    }

    @GetMapping("/add-station")
    public String addStation(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        return "add-station";
    }

    @GetMapping("/edit-station")
    public String editStation(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        return "edit-station";
    }

    @GetMapping("/station-rates")
    public String stationRates(Model model) {
        model.addAttribute("backendUrl", backendUrl);
        return "station-rates";
    }
}

