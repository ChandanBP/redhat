package com.example.demo.controller;

import com.example.demo.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/consume")
public class ConsumerController {

    @Autowired
    protected ConsumerService consumerService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void consume()throws Exception{
        consumerService.consume();
    }
}
