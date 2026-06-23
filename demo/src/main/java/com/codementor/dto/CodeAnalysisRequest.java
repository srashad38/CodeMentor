package com.codementor.dto;

public class CodeAnalysisRequest {

    private String language;
    private String code;

    public CodeAnalysisRequest() {
    }

    public CodeAnalysisRequest(String language, String code) {
        this.language = language;
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public String getCode() {
        return code;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setCode(String code) {
        this.code = code;
    }
}