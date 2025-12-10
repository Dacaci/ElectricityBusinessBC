package com.eb.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VerifySuccessController {

    @GetMapping("/verify-success")
    public String verifySuccess(Model model) {
        return "verify-success";
    }
}

