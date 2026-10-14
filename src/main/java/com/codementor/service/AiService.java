package com.codementor.service;

import com.codementor.dto.CodeAnalysisRequest;
import com.codementor.dto.CodeAnalysisResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiService {

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.model}")
    private String model;

    @Value("${ai.provider:AI provider}")
    private String provider;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public CodeAnalysisResponse analyzeWithAi(CodeAnalysisRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GROQ_API_KEY is not configured. Create a Groq API key and restart the application."
            );
        }

        try {
            String prompt = buildPrompt(request);

            String requestBody = objectMapper.writeValueAsString(
                    new ChatCompletionRequest(
                            model,
                            List.of(
                                    new Message("system", "You are an educational AI debugging tutor. Help students learn. Do not simply give answers first."),
                                    new Message("user", prompt)
                            )
                    )
            );

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(provider + " API error: " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {
                throw new RuntimeException(provider + " returned no analysis result.");
            }

            String content = choices.get(0).path("message").path("content").asText();

            if (content.isBlank()) {
                throw new RuntimeException(provider + " returned an empty analysis result.");
            }

            return parseAiResponse(content);

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze code with AI: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(CodeAnalysisRequest request) {
        return """
                Analyze the following student code.

                Programming language:
                %s

                Code:
                ```%s
                %s
                ```

                Return the response in this exact format:

                SUMMARY:
                [brief summary]

                ISSUES:
                - [issue 1]
                - [issue 2]
                - [issue 3]

                HINTS:
                - [hint 1]
                - [hint 2]
                - [hint 3]

                TEST_CASES:
                - [test case 1]
                - [test case 2]
                - [test case 3]

                EXPLANATION:
                [clear explanation of the mistake]

                CORRECTED_CODE:
                ```%s
                [corrected code here]
                ```

                MISTAKE_CATEGORY:
                [one category such as Loop Error, Syntax Error, Logic Error, Recursion Error, Data Structure Error, Input Validation Error, or Performance Issue]

                LEARNING_RECOMMENDATION:
                [one specific mini-lesson or practice exercise that helps the student improve this weakness]
                """.formatted(
                request.getLanguage(),
                request.getLanguage(),
                request.getCode(),
                request.getLanguage()
        );
    }

    private CodeAnalysisResponse parseAiResponse(String aiText) {
        String summary = extractSection(aiText, "SUMMARY:", "ISSUES:");
        String issuesText = extractSection(aiText, "ISSUES:", "HINTS:");
        String hintsText = extractSection(aiText, "HINTS:", "TEST_CASES:");
        String testCasesText = extractSection(aiText, "TEST_CASES:", "EXPLANATION:");
        String explanation = extractSection(aiText, "EXPLANATION:", "CORRECTED_CODE:");
        String correctedCode = extractSection(aiText, "CORRECTED_CODE:", "MISTAKE_CATEGORY:");
        String category = extractSection(aiText, "MISTAKE_CATEGORY:", "LEARNING_RECOMMENDATION:");
        String recommendation = extractToEnd(aiText, "LEARNING_RECOMMENDATION:");

        return new CodeAnalysisResponse(
                summary,
                toList(issuesText),
                toList(hintsText),
                toList(testCasesText),
                explanation,
                cleanedCode(correctedCode),
                category.isBlank() ? "General Debugging" : category,
                recommendation.isBlank() ? "Practice tracing this code by hand with a small input." : recommendation
        );
    }

    private String extractSection(String text, String start, String end) {
        int startIndex = text.indexOf(start);
        int endIndex = text.indexOf(end);

        if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
            return "";
        }

        return text.substring(startIndex + start.length(), endIndex).trim();
    }

    private List<String> toList(String text) {
        List<String> list = new ArrayList<>();

        for (String line : text.split("\n")) {
            String cleaned = line.trim();

            if (cleaned.startsWith("-")) {
                cleaned = cleaned.substring(1).trim();
            }

            if (!cleaned.isBlank()) {
                list.add(cleaned);
            }
        }

        return list;
    }

    private String extractToEnd(String text, String start) {
        int startIndex = text.indexOf(start);
        return startIndex == -1 ? "" : text.substring(startIndex + start.length()).trim();
    }

    private String cleanedCode(String code) {
        return code
                .replace("```java", "")
                .replace("```python", "")
                .replace("```javascript", "")
                .replace("```cpp", "")
                .replace("```", "")
                .trim();
    }

    private record ChatCompletionRequest(String model, List<Message> messages) {
    }

    private record Message(String role, String content) {
    }
}
