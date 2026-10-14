package com.codementor.dto;

import java.util.List;

public class CodeAnalysisResponse {

    private String summary;
    private List<String> issues;
    private List<String> hints;
    private List<String> testCases;
    private String explanation;
    private String correctedCode;
    private String mistakeCategory;
    private String learningRecommendation;
    private List<String> staticFindings = List.of();

    public CodeAnalysisResponse() {
    }

    public CodeAnalysisResponse(
            String summary,
            List<String> issues,
            List<String> hints,
            List<String> testCases,
            String explanation,
            String correctedCode
    ) {
        this(summary, issues, hints, testCases, explanation, correctedCode,
                "General Debugging", "Practice tracing code one step at a time.");
    }

    public CodeAnalysisResponse(
            String summary,
            List<String> issues,
            List<String> hints,
            List<String> testCases,
            String explanation,
            String correctedCode,
            String mistakeCategory,
            String learningRecommendation
    ) {
        this.summary = summary;
        this.issues = issues;
        this.hints = hints;
        this.testCases = testCases;
        this.explanation = explanation;
        this.correctedCode = correctedCode;
        this.mistakeCategory = mistakeCategory;
        this.learningRecommendation = learningRecommendation;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getIssues() {
        return issues;
    }

    public List<String> getHints() {
        return hints;
    }

    public List<String> getTestCases() {
        return testCases;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getCorrectedCode() {
        return correctedCode;
    }

    public String getMistakeCategory() { return mistakeCategory; }
    public String getLearningRecommendation() { return learningRecommendation; }
    public List<String> getStaticFindings() { return staticFindings; }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setIssues(List<String> issues) {
        this.issues = issues;
    }

    public void setHints(List<String> hints) {
        this.hints = hints;
    }

    public void setTestCases(List<String> testCases) {
        this.testCases = testCases;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setCorrectedCode(String correctedCode) {
        this.correctedCode = correctedCode;
    }

    public void setMistakeCategory(String mistakeCategory) { this.mistakeCategory = mistakeCategory; }
    public void setLearningRecommendation(String learningRecommendation) { this.learningRecommendation = learningRecommendation; }
    public void setStaticFindings(List<String> staticFindings) { this.staticFindings = staticFindings; }
}
