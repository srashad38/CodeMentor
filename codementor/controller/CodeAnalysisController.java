package com.codementor.controller;


import com.codementor.dto.CodeAnalysisRequest;
import com.codementor.dto.CodeAnalysisResponse;
import com.codementor.service.CodeAnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CodeAnalysisController {

    private final CodeAnalysisService codeAnalysisService;

    public CodeAnalysisController(CodeAnalysisService codeAnalysisService) {
        this.codeAnalysisService = codeAnalysisService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("request", new CodeAnalysisRequest());
        return "index";
    }

    @PostMapping("/analyze")
    public String analyzeCode(
            @ModelAttribute CodeAnalysisRequest request,
            Model model
    ) {
        CodeAnalysisResponse response = codeAnalysisService.analyzeCode(request);

        model.addAttribute("request", request);
        model.addAttribute("result", response);

        return "index";
    }
}