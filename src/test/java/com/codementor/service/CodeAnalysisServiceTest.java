package com.codementor.service;

import com.codementor.dto.CodeAnalysisRequest;
import com.codementor.dto.CodeAnalysisResponse;
import com.codementor.repository.CodeSubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeAnalysisServiceTest {

    @Mock private AiService aiService;
    @Mock private CodeSubmissionRepository repository;
    @Mock private StaticAnalysisService staticAnalysisService;
    private CodeAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new CodeAnalysisService(aiService, repository, staticAnalysisService);
    }

    @Test
    void rejectsEmptyCode() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.analyzeCode(new CodeAnalysisRequest("Java", "  "))
        );
        assertEquals("Code cannot be empty.", error.getMessage());
    }

    @Test
    void rejectsUnsupportedLanguage() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.analyzeCode(new CodeAnalysisRequest("Brainf***", "++++"))
        );
    }

    @Test
    void rejectsOversizedCode() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.analyzeCode(new CodeAnalysisRequest("Java", "x".repeat(20_001)))
        );
    }

    @Test
    void analyzesAndSavesValidCode() {
        CodeAnalysisResponse response = new CodeAnalysisResponse(
                "Summary", List.of("Issue"), List.of("Hint"), List.of("Test"),
                "Explanation", "fixed();"
        );
        when(aiService.analyzeWithAi(any())).thenReturn(response);
        when(staticAnalysisService.inspect(any())).thenReturn(List.of("Static finding"));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CodeAnalysisService.AnalysisOutcome actual = service.analyzeCode(
                new CodeAnalysisRequest("Java", "  broken();  ")
        );

        assertEquals(response, actual.response());
        verify(repository).save(any());
    }
}
