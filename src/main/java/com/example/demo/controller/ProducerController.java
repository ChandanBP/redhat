package com.example.demo.controller;

import com.example.demo.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/produce")
public class ProducerController {

    @Autowired
    protected ProducerService producerService;

    @GetMapping(value = "/emails")
    public void generateEmailSubscription()throws Exception {
        producerService.pushEmails();
    }

    @PutMapping(value = "email")
    public void pushEmail(@RequestParam String email){
        if(email==null || email.isEmpty()) return;
    }

    @GetMapping
    public void startBatch()throws Exception{
        producerService.startBatch();
    }
}
