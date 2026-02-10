package com.bfhl.bfhl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.*;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_URL =
            "https://api.openai.com/v1/chat/completions";

    public String askAI(String question) {

        // ✅ Safety check: API key must exist
        if (apiKey == null || apiKey.isBlank()) {
            return "API_KEY_NOT_LOADED";
        }

        // ✅ Timeout configuration (VERY IMPORTANT)
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(10000);

        RestTemplate restTemplate = new RestTemplate(factory);

        // -------- Request Body --------
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", question + " Answer in one word only.");

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini"); // ✅ supported model
        body.put("messages", List.of(message));
        body.put("max_tokens", 10);

        // -------- Headers --------
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(OPENAI_URL, request, Map.class);

            Map responseBody = response.getBody();

            // ✅ Handle OpenAI error responses
            if (responseBody == null || !responseBody.containsKey("choices")) {
                return "OpenAI_Error";
            }

            List choices = (List) responseBody.get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map msg = (Map) firstChoice.get("message");

            return msg.get("content").toString().trim();

        } catch (Exception e) {
            // ✅ Prevents API crash
            return "OpenAI_Exception";
        }
    }
}