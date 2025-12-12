package com.eb.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VerifyController {

    @GetMapping("/verify")
    public String verify(Model model) {
        return "verify";
    }
}
