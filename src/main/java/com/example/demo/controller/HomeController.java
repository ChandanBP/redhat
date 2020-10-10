package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/")
public class HomeController {

    @GetMapping
    public String home() {
        return "Hello,Welcome to smart shop. New experience in online shopping";
    }
}
