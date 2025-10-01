package com.eb.eb_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/index.html";
    }
    
    @GetMapping("/users")
    public String users() {
        return "forward:/index.html";
    }
    
    @GetMapping("/stations")
    public String stations() {
        return "forward:/index.html";
    }
    
    @GetMapping("/locations")
    public String locations() {
        return "forward:/index.html";
    }
}
