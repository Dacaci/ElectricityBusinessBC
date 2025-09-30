package com.eb.eb_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> test() {
        return ResponseEntity.ok(Map.of(
            "message", "API Electricity Business fonctionne !",
            "timestamp", LocalDateTime.now(),
            "status", "OK"
        ));
    }
}
