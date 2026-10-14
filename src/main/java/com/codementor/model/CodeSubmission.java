package com.codementor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

@Entity
public class CodeSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String language;

    @Lob
    @Column(nullable = false)
    private String code;

    @Lob private String summary;
    @Lob private String issues;
    @Lob private String hints;
    @Lob private String testCases;
    @Lob private String explanation;
    @Lob private String correctedCode;
    @Column(length = 80) private String mistakeCategory;
    @Lob private String learningRecommendation;
    @Lob private String staticFindings;
    @Column(length = 30) private String progressStatus = "NOT_REPORTED";
    private Integer hintsViewed;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void setCreatedAt() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getLanguage() { return language; }
    public String getCode() { return code; }
    public String getSummary() { return summary; }
    public String getIssues() { return issues; }
    public String getHints() { return hints; }
    public String getTestCases() { return testCases; }
    public String getExplanation() { return explanation; }
    public String getCorrectedCode() { return correctedCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getMistakeCategory() { return mistakeCategory; }
    public String getLearningRecommendation() { return learningRecommendation; }
    public String getStaticFindings() { return staticFindings; }
    public String getProgressStatus() { return progressStatus; }
    public int getHintsViewed() { return hintsViewed == null ? 0 : hintsViewed; }

    public void setLanguage(String language) { this.language = language; }
    public void setCode(String code) { this.code = code; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setIssues(String issues) { this.issues = issues; }
    public void setHints(String hints) { this.hints = hints; }
    public void setTestCases(String testCases) { this.testCases = testCases; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public void setCorrectedCode(String correctedCode) { this.correctedCode = correctedCode; }
    public void setMistakeCategory(String mistakeCategory) { this.mistakeCategory = mistakeCategory; }
    public void setLearningRecommendation(String learningRecommendation) { this.learningRecommendation = learningRecommendation; }
    public void setStaticFindings(String staticFindings) { this.staticFindings = staticFindings; }
    public void setProgressStatus(String progressStatus) { this.progressStatus = progressStatus; }
    public void setHintsViewed(int hintsViewed) { this.hintsViewed = hintsViewed; }
}
