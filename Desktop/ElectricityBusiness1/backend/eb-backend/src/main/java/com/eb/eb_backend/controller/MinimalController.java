package com.eb.eb_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MinimalController {

    @GetMapping("/test")
    public String test() {
        return "API Test OK - " + java.time.LocalDateTime.now();
    }
}






















