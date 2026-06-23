package com.codementor.service;

import com.codementor.dto.CodeAnalysisRequest;
import com.codementor.dto.CodeAnalysisResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodeAnalysisService {

    public CodeAnalysisResponse analyzeCode(CodeAnalysisRequest request) {

        return new CodeAnalysisResponse(
                "Your code was analyzed successfully.",
                List.of(
                        "Possible logic error detected.",
                        "Variable naming could be clearer.",
                        "No test cases were provided."
                ),
                List.of(
                        "Check whether your loop condition stops at the correct value.",
                        "Print the variable values during each loop iteration.",
                        "Try testing the smallest possible input."
                ),
                List.of(
                        "Input: 0 → Expected output: check edge case",
                        "Input: 1 → Expected output: check base case",
                        "Input: large number → Expected output: check performance"
                ),
                "The main issue appears to be related to program logic rather than syntax. The code may run, but it may not produce the expected result for all inputs.",
                "// Corrected code will appear here after AI integration."
        );
    }
}