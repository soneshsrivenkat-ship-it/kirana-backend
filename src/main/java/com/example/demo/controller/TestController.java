package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    /**
     * Testing the backend is running or not.
     * @return
     */

    @GetMapping("/api/test")
    public String testApi() {
        return "Kirana Backend Running ";
    }
}

