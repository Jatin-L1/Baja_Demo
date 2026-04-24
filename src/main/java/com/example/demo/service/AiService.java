package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${groq.api.key:}")
    private String apiKey;

    // Singleton RestTemplate — not created on every call
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.1-8b-instant";

    /**
     * Sends a question to the Groq AI API and returns a single-word answer.
     * Post-processing ensures the response is always exactly one word.
     */
    public String askAI(String question) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GROQ_API_KEY environment variable is not configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", MODEL,
                "temperature", 0,          // deterministic output
                "max_tokens", 10,          // allow only a tiny response
                "messages", List.of(
                        Map.of("role", "system",
                               "content", "You are a factual assistant. " +
                                          "Answer ONLY with a single word. " +
                                          "No punctuation. No explanations. " +
                                          "Just the one-word answer."),
                        Map.of("role", "user", "content", question)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, entity, Map.class);

        if (response.getBody() != null) {
            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message != null && message.get("content") != null) {
                    // Post-process: take only the first word and strip all punctuation/whitespace
                    String raw = message.get("content").toString().trim();
                    return extractFirstWord(raw);
                }
            }
        }

        throw new RuntimeException("AI service returned an empty response");
    }

    /**
     * Extracts the first word from the AI response, stripping any punctuation.
     * e.g. "Mumbai." → "Mumbai", "New Delhi" → "New" (assignment says single-word)
     */
    private String extractFirstWord(String text) {
        if (text == null || text.isBlank()) return "Unknown";
        // Split on whitespace, take first token, strip non-alphanumeric chars
        String[] tokens = text.split("\\s+");
        return tokens[0].replaceAll("[^a-zA-Z0-9]", "");
    }
}
