package com.bhavana.aidoc.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    private final Client geminiClient;
    private final RestTemplate restTemplate = new RestTemplate();

    public GeminiService(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    public String generateText(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "API key is missing. Please configure a valid API key in application.properties or set GEMINI_API_KEY.";
        }

        // Handle Groq API key (starts with gsk_)
        if (apiKey.startsWith("gsk_")) {
            return callGroqApi(prompt);
        }

        List<String> modelsToTry = List.of("gemini-2.0-flash");
        Set<String> uniqueModels = new LinkedHashSet<>(modelsToTry);
        Exception lastException = null;

        // 1. Try SDK call with 429 rate-limit backoff retry
        for (String m : uniqueModels) {
            for (int attempt = 1; attempt <= 4; attempt++) {
                try {
                    System.out.println("DEBUG: Trying SDK generateContent with model: " + m + " (attempt " + attempt + ")");
                    GenerateContentResponse response = geminiClient.models.generateContent(m, prompt, null);
                    if (response != null && response.text() != null && !response.text().isBlank()) {
                        System.out.println("DEBUG: SDK Call Succeeded with model: " + m);
                        return response.text();
                    }
                } catch (Exception e) {
                    lastException = e;
                    String msg = e.getMessage() != null ? e.getMessage() : "";
                    System.err.println("DEBUG: SDK Call Failed for model " + m + " (attempt " + attempt + "): " + msg);
                    if (msg.contains("429") || msg.contains("Quota exceeded") || msg.contains("retry")) {
                        try {
                            long sleepMs = 5000L * attempt;
                            System.out.println("DEBUG: Rate limit hit. Backing off " + (sleepMs / 1000) + " seconds...");
                            Thread.sleep(sleepMs);
                        } catch (InterruptedException ignored) {}
                    } else {
                        break; // Non-rate-limit error (e.g. 404), try next model
                    }
                }
            }
        }

        // 2. Try REST POST with Bearer Token & x-goog-api-key
        for (String m : uniqueModels) {
            String result = callRestGemini(m, prompt);
            if (result != null && !result.isBlank() && !result.startsWith("ERROR:")) {
                return result;
            }
        }

        String errMsg = lastException != null ? lastException.getMessage() : "Unknown error";
        if (errMsg.contains("429") || errMsg.contains("Quota exceeded")) {
            return "Notice: Google AI Studio Free Tier rate limit reached (15 requests/min).\n\n" +
                   "Please wait 10-20 seconds and click 'Generate Documentation' again.";
        }
        return "Error calling Gemini API: " + errMsg;
    }

    private String callRestGemini(String targetModel, String prompt) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + targetModel + ":generateContent";
            String jsonBody = """
                    {
                      "contents": [{
                        "parts": [{"text": "%s"}]
                      }]
                    }
                    """.formatted(prompt.replace("\"", "\\\"").replace("\n", "\\n"));

            // Try x-goog-api-key header
            HttpHeaders headers1 = new HttpHeaders();
            headers1.setContentType(MediaType.APPLICATION_JSON);
            headers1.set("x-goog-api-key", apiKey);
            try {
                ResponseEntity<String> res = restTemplate.postForEntity(url, new HttpEntity<>(jsonBody, headers1), String.class);
                if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                    return extractTextFromJsonResponse(res.getBody());
                }
            } catch (Exception ignored) {}

            // Try Bearer token
            HttpHeaders headers2 = new HttpHeaders();
            headers2.setContentType(MediaType.APPLICATION_JSON);
            headers2.setBearerAuth(apiKey);
            try {
                ResponseEntity<String> res = restTemplate.postForEntity(url, new HttpEntity<>(jsonBody, headers2), String.class);
                if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                    return extractTextFromJsonResponse(res.getBody());
                }
            } catch (Exception ignored) {}
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
        return null;
    }

    private String callGroqApi(String prompt) {
        try {
            String url = "https://api.groq.com/openai/v1/chat/completions";
            Map<String, Object> requestBody = new HashMap<>();
            String targetModel = (model != null && model.startsWith("llama")) ? model : "llama-3.3-70b-versatile";
            requestBody.put("model", targetModel);
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> res = restTemplate.postForEntity(url, entity, Map.class);

            if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                List choices = (List) res.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map firstChoice = (Map) choices.get(0);
                    Map message = (Map) firstChoice.get("message");
                    if (message != null && message.get("content") != null) {
                        return message.get("content").toString();
                    }
                }
            }
        } catch (Exception e) {
            return "Error calling Groq API (" + e.getMessage() + "). Please verify your Groq API key.";
        }
        return "Error: Empty response from Groq API.";
    }

    private String extractTextFromJsonResponse(String json) {
        try {
            int textIdx = json.indexOf("\"text\": \"");
            if (textIdx != -1) {
                int start = textIdx + 9;
                int end = json.indexOf("\"", start);
                if (end != -1) {
                    return json.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
                }
            }
        } catch (Exception ignored) {}
        return json;
    }
}