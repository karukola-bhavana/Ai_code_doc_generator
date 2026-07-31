package com.bhavana.aidoc.controller;

import com.bhavana.aidoc.service.GeminiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping("/test")
    public String test(@RequestParam String prompt) {
        return geminiService.generateText(prompt);
    }
}