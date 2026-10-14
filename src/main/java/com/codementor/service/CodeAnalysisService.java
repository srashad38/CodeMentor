package com.codementor.service;

import com.codementor.dto.CodeAnalysisRequest;
import com.codementor.dto.CodeAnalysisResponse;
import com.codementor.model.AnalysisResult;
import com.codementor.model.CodeSubmission;
import com.codementor.repository.CodeSubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class CodeAnalysisService {

    private final AiService aiService;
    private final CodeSubmissionRepository repository;
    private final StaticAnalysisService staticAnalysisService;
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of(
            "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "Go", "Rust"
    );
    private static final int MAX_CODE_LENGTH = 20_000;

    public CodeAnalysisService(AiService aiService, CodeSubmissionRepository repository,
                               StaticAnalysisService staticAnalysisService) {
        this.aiService = aiService;
        this.repository = repository;
        this.staticAnalysisService = staticAnalysisService;
    }

    public AnalysisOutcome analyzeCode(CodeAnalysisRequest request) {
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new IllegalArgumentException("Code cannot be empty.");
        }

        if (request.getLanguage() == null || request.getLanguage().isBlank()) {
            throw new IllegalArgumentException("Programming language is required.");
        }

        if (!SUPPORTED_LANGUAGES.contains(request.getLanguage())) {
            throw new IllegalArgumentException("Unsupported programming language.");
        }

        if (request.getCode().length() > MAX_CODE_LENGTH) {
            throw new IllegalArgumentException("Code must be 20,000 characters or fewer.");
        }

        request.setCode(request.getCode().strip());
        CodeAnalysisResponse response = aiService.analyzeWithAi(request);
        response.setStaticFindings(staticAnalysisService.inspect(request));
        CodeSubmission saved = repository.save(toSubmission(request, response));
        return new AnalysisOutcome(saved.getId(), response);
    }

    public List<CodeSubmission> recentSubmissions() {
        return repository.findTop10ByOrderByCreatedAtDesc();
    }

    public Optional<SavedAnalysis> findSubmission(long id) {
        return repository.findById(id).map(submission -> new SavedAnalysis(
                submission,
                AnalysisResult.from(submission)
        ));
    }

    public void recordProgress(long id, String status, int hintsViewed) {
        CodeSubmission submission = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found."));
        if (!Set.of("SOLVED", "NEEDS_PRACTICE").contains(status)) {
            throw new IllegalArgumentException("Invalid progress status.");
        }
        submission.setProgressStatus(status);
        submission.setHintsViewed(Math.max(0, Math.min(hintsViewed, 3)));
        repository.save(submission);
    }

    public LearningAnalytics analytics() {
        List<CodeSubmission> submissions = repository.findAll();
        Map<String, Long> categories = new LinkedHashMap<>();
        submissions.stream()
                .map(CodeSubmission::getMistakeCategory)
                .filter(value -> value != null && !value.isBlank())
                .forEach(category -> categories.merge(category, 1L, Long::sum));
        long solved = submissions.stream().filter(s -> "SOLVED".equals(s.getProgressStatus())).count();
        long practice = submissions.stream().filter(s -> "NEEDS_PRACTICE".equals(s.getProgressStatus())).count();
        String topWeakness = categories.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("No pattern yet");
        String recommendation = submissions.stream()
                .filter(s -> topWeakness.equals(s.getMistakeCategory()))
                .map(CodeSubmission::getLearningRecommendation)
                .filter(value -> value != null && !value.isBlank())
                .findFirst().orElse("Complete more analyses to receive a personalized mini-lesson.");
        return new LearningAnalytics(submissions.size(), solved, practice, topWeakness, recommendation, categories);
    }

    private CodeSubmission toSubmission(CodeAnalysisRequest request, CodeAnalysisResponse response) {
        CodeSubmission submission = new CodeSubmission();
        submission.setLanguage(request.getLanguage());
        submission.setCode(request.getCode());
        submission.setSummary(response.getSummary());
        submission.setIssues(String.join("\n", response.getIssues()));
        submission.setHints(String.join("\n", response.getHints()));
        submission.setTestCases(String.join("\n", response.getTestCases()));
        submission.setExplanation(response.getExplanation());
        submission.setCorrectedCode(response.getCorrectedCode());
        submission.setMistakeCategory(response.getMistakeCategory());
        submission.setLearningRecommendation(response.getLearningRecommendation());
        submission.setStaticFindings(String.join("\n", response.getStaticFindings()));
        return submission;
    }

    public record AnalysisOutcome(Long submissionId, CodeAnalysisResponse response) { }
    public record SavedAnalysis(CodeSubmission submission, AnalysisResult result) {
    }
    public record LearningAnalytics(long totalSubmissions, long solved, long needsPractice,
                                    String topWeakness, String recommendation,
                                    Map<String, Long> categoryCounts) { }
}
