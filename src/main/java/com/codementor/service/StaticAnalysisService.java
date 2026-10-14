package com.codementor.service;

import com.codementor.dto.CodeAnalysisRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class StaticAnalysisService {
    private static final Pattern EMPTY_CATCH = Pattern.compile("catch\\s*\\([^)]*\\)\\s*\\{\\s*}", Pattern.DOTALL);
    private static final Pattern ENDLESS_WHILE = Pattern.compile("while\\s*\\(\\s*(true|1)\\s*\\)", Pattern.CASE_INSENSITIVE);

    public List<String> inspect(CodeAnalysisRequest request) {
        String code = request.getCode();
        List<String> findings = new ArrayList<>();

        if (EMPTY_CATCH.matcher(code).find()) findings.add("Empty catch block may hide runtime failures.");
        if (ENDLESS_WHILE.matcher(code).find()) findings.add("Potential unbounded loop detected; verify that every path can exit.");
        if (code.contains("TODO") || code.contains("FIXME")) findings.add("Unresolved TODO/FIXME marker remains in the submitted code.");
        if (code.lines().anyMatch(line -> line.length() > 140)) findings.add("Very long line detected; split complex expressions to make debugging easier.");
        if (request.getLanguage().equals("Java") && code.contains("==") && code.contains("String")) {
            findings.add("Check String comparisons: Java usually requires .equals(...) instead of ==.");
        }
        if (request.getLanguage().equals("Python") && code.contains("except:") && !code.contains("except Exception")) {
            findings.add("Bare except clause catches every error; catch a specific exception where possible.");
        }
        if (findings.isEmpty()) findings.add("No obvious pattern-level problem was found; review the AI logic analysis and generated tests.");
        return findings;
    }
}
