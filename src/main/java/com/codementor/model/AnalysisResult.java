package com.codementor.model;

import com.codementor.dto.CodeAnalysisResponse;

import java.util.Arrays;
import java.util.List;

public record AnalysisResult(
        String summary,
        List<String> issues,
        List<String> hints,
        List<String> testCases,
        String explanation,
        String correctedCode,
        String mistakeCategory,
        String learningRecommendation,
        List<String> staticFindings
) {
    public static AnalysisResult from(CodeSubmission submission) {
        return new AnalysisResult(
                submission.getSummary(), lines(submission.getIssues()),
                lines(submission.getHints()), lines(submission.getTestCases()),
                submission.getExplanation(), submission.getCorrectedCode(),
                submission.getMistakeCategory(), submission.getLearningRecommendation(),
                lines(submission.getStaticFindings())
        );
    }

    public static AnalysisResult from(CodeAnalysisResponse response) {
        return new AnalysisResult(
                response.getSummary(), response.getIssues(), response.getHints(),
                response.getTestCases(), response.getExplanation(), response.getCorrectedCode(),
                response.getMistakeCategory(), response.getLearningRecommendation(), response.getStaticFindings()
        );
    }

    private static List<String> lines(String value) {
        return value == null || value.isBlank() ? List.of() : Arrays.asList(value.split("\\R"));
    }
}
