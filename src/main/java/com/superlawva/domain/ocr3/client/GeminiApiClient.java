package com.superlawva.domain.ocr3.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Alternative implementation using Google's Generative AI REST API
 * This is a more robust approach for production use
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiApiClient {
<<<<<<< HEAD
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${gemini.api-key}")
    private String apiKey;
    
    @Value("${gemini.model-name}")
    private String modelName;
    
    @Value("${gemini.api-url:https://generativelanguage.googleapis.com}")
    private String apiBaseUrl;
    
    public <T> T generateContent(String prompt, Class<T> responseType) throws Exception {
        String url = String.format("%s/v1beta/models/%s:generateContent?key=%s", 
                apiBaseUrl, modelName, apiKey);
        
        Map<String, Object> requestBody = buildRequestBody(prompt);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<GeminiApiResponse> response = restTemplate.postForEntity(
                    url, entity, GeminiApiResponse.class);
            
=======

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model-name}")
    private String modelName;

    @Value("${gemini.api-url:https://generativelanguage.googleapis.com}")
    private String apiBaseUrl;

    public <T> T generateContent(String prompt, Class<T> responseType) throws Exception {
        String url = String.format("%s/v1beta/models/%s:generateContent?key=%s",
                apiBaseUrl, modelName, apiKey);

        Map<String, Object> requestBody = buildRequestBody(prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<GeminiApiResponse> response = restTemplate.postForEntity(
                    url, entity, GeminiApiResponse.class);

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String jsonContent = extractJsonContent(response.getBody());
                return objectMapper.readValue(jsonContent, responseType);
            } else {
                throw new RuntimeException("Failed to get valid response from Gemini API");
            }
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw e;
        }
    }
<<<<<<< HEAD
    
    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // Contents
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        
=======

    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();

        // Contents
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        List<Map<String, Object>> parts = new ArrayList<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        parts.add(part);
<<<<<<< HEAD
        
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);
        
=======

        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        // Generation Config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.1);
        generationConfig.put("topK", 1);
        generationConfig.put("topP", 1);
        generationConfig.put("maxOutputTokens", 8192);
        generationConfig.put("responseMimeType", "application/json");
        requestBody.put("generationConfig", generationConfig);
<<<<<<< HEAD
        
        // Safety Settings
        List<Map<String, String>> safetySettings = new ArrayList<>();
        String[] categories = {
            "HARM_CATEGORY_HARASSMENT",
            "HARM_CATEGORY_HATE_SPEECH",
            "HARM_CATEGORY_SEXUALLY_EXPLICIT",
            "HARM_CATEGORY_DANGEROUS_CONTENT"
        };
        
=======

        // Safety Settings
        List<Map<String, String>> safetySettings = new ArrayList<>();
        String[] categories = {
                "HARM_CATEGORY_HARASSMENT",
                "HARM_CATEGORY_HATE_SPEECH",
                "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                "HARM_CATEGORY_DANGEROUS_CONTENT"
        };

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        for (String category : categories) {
            Map<String, String> setting = new HashMap<>();
            setting.put("category", category);
            setting.put("threshold", "BLOCK_NONE");
            safetySettings.add(setting);
        }
        requestBody.put("safetySettings", safetySettings);
<<<<<<< HEAD
        
        return requestBody;
    }
    
=======

        return requestBody;
    }

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
    private String extractJsonContent(GeminiApiResponse response) {
        if (response.getCandidates() != null && !response.getCandidates().isEmpty()) {
            GeminiApiResponse.Candidate candidate = response.getCandidates().get(0);
            if (candidate.getContent() != null && candidate.getContent().getParts() != null) {
                for (GeminiApiResponse.Part part : candidate.getContent().getParts()) {
                    if (part.getText() != null) {
                        return part.getText();
                    }
                }
            }
        }
        throw new RuntimeException("No valid content found in Gemini response");
    }
<<<<<<< HEAD
    
=======

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
    // Inner classes for Gemini API response structure
    @lombok.Data
    public static class GeminiApiResponse {
        private List<Candidate> candidates;
        private UsageMetadata usageMetadata;
<<<<<<< HEAD
        
=======

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        @lombok.Data
        public static class Candidate {
            private Content content;
            private String finishReason;
            private Integer index;
            private List<SafetyRating> safetyRatings;
        }
<<<<<<< HEAD
        
=======

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        @lombok.Data
        public static class Content {
            private List<Part> parts;
            private String role;
        }
<<<<<<< HEAD
        
=======

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        @lombok.Data
        public static class Part {
            private String text;
        }
<<<<<<< HEAD
        
=======

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        @lombok.Data
        public static class SafetyRating {
            private String category;
            private String probability;
        }
<<<<<<< HEAD
        
=======

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        @lombok.Data
        public static class UsageMetadata {
            private Integer promptTokenCount;
            private Integer candidatesTokenCount;
            private Integer totalTokenCount;
        }
    }
}