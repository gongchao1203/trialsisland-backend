package com.trialsisland.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello, Welcome to Trials Island Backend!");
        result.put("status", "success");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

}