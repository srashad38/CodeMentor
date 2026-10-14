package com.codementor.controller;

import com.codementor.dto.CodeAnalysisRequest;
import com.codementor.dto.CodeAnalysisResponse;
import com.codementor.service.CodeAnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class CodeAnalysisController {

    private final CodeAnalysisService codeAnalysisService;

    public CodeAnalysisController(CodeAnalysisService codeAnalysisService) {
        this.codeAnalysisService = codeAnalysisService;
    }

    @GetMapping("/")
    public String home(Model model) {
        CodeAnalysisRequest request = new CodeAnalysisRequest();
        request.setLanguage("Java");
        model.addAttribute("request", request);
        addCommonAttributes(model);
        return "index";
    }

    @PostMapping("/analyze")
    public String analyzeCode(@ModelAttribute CodeAnalysisRequest request, Model model) {
        model.addAttribute("request", request);
        addCommonAttributes(model);

        try {
            CodeAnalysisService.AnalysisOutcome outcome = codeAnalysisService.analyzeCode(request);
            model.addAttribute("result", outcome.response());
            model.addAttribute("submissionId", outcome.submissionId());
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute(
                    "errorMessage",
                    "The AI analysis could not be completed. " + rootCauseMessage(e)
            );
        }

        return "index";
    }

    @GetMapping("/history/{id}")
    public String viewHistory(@PathVariable long id, Model model) {
        CodeAnalysisService.SavedAnalysis saved = codeAnalysisService.findSubmission(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Analysis not found"));

        model.addAttribute("request", new CodeAnalysisRequest(
                saved.submission().getLanguage(), saved.submission().getCode()
        ));
        model.addAttribute("result", saved.result());
        model.addAttribute("viewingHistory", true);
        model.addAttribute("submissionId", saved.submission().getId());
        model.addAttribute("progressStatus", saved.submission().getProgressStatus());
        addCommonAttributes(model);
        return "index";
    }

    @PostMapping("/history/{id}/progress")
    public String recordProgress(@PathVariable long id,
                                 @RequestParam String status,
                                 @RequestParam(defaultValue = "0") int hintsViewed) {
        codeAnalysisService.recordProgress(id, status, hintsViewed);
        return "redirect:/history/" + id;
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("analytics", codeAnalysisService.analytics());
        model.addAttribute("history", codeAnalysisService.recentSubmissions());
        return "analytics";
    }

    private void addCommonAttributes(Model model) {
        model.addAttribute("history", codeAnalysisService.recentSubmissions());
    }

    private String rootCauseMessage(Exception exception) {
        Throwable cause = exception;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        String message = cause.getMessage();
        return message == null || message.isBlank()
                ? "Please try again."
                : message;
    }
}
