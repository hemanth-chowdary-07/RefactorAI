package com.refactorai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String getRefactoringSuggestion(String originalCode, String codeSmellType, String description) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Build request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // Build request body
            String prompt = buildPrompt(originalCode, codeSmellType, description);
            Map<String, Object> requestBody = buildRequestBody(prompt);

            // Make API call
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_API_URL, request, Map.class);

            // Parse response
            return parseResponse(response.getBody());

        } catch (Exception e) {
            return "Error getting AI suggestion: " + e.getMessage();
        }
    }

    private String buildPrompt(String code, String smellType, String description) {
        return String.format(
                "You are a Java code refactoring expert.\n\n" +
                        "TASK: Refactor the following Java code to fix this issue:\n" +
                        "Issue Type: %s\n" +
                        "Issue Description: %s\n\n" +
                        "Original Code:\n```java\n%s\n```\n\n" +
                        "INSTRUCTIONS:\n" +
                        "1. Return ONLY the complete refactored Java code\n" +
                        "2. Do NOT include explanations, markdown, or comments outside the code\n" +
                        "3. Ensure the refactored code compiles and runs correctly\n" +
                        "4. Keep the same class name and method signatures where possible\n" +
                        "5. Start your response with the Java code directly\n\n" +
                        "REFACTORED CODE:",
                smellType, description, code
        );
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.3-70b-versatile");
        body.put("temperature", 0.7);
        body.put("max_tokens", 1000);

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are an expert Java developer who provides clear refactoring suggestions.");

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        body.put("messages", List.of(systemMessage, userMessage));

        return body;
    }

    private String parseResponse(Map<String, Object> responseBody) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        } catch (Exception e) {
            return "Error parsing AI response: " + e.getMessage();
        }
        return "No response from AI";
    }
}