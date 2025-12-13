package com.eb.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        // Approche "Map-First" : rediriger directement vers la carte
        return "redirect:/map";
    }
}

