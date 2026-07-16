package com.agromind.auction.ai.service;

import com.agromind.auction.ai.dto.SoilAnalysisRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    // Automatically grabs the key you put in application.yml.
    // The colon ":" provides a default fallback so the app won't crash on boot if the key is missing!
    @Value("${gemini.api.key:MISSING_API_KEY}")
    private String geminiApiKey;

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public String analyzeSoil(SoilAnalysisRequest request) {
        // 1. We construct a highly specific Prompt Engineering string
        String prompt = String.format(
                "Act as an expert agronomist in %s, India. " +
                        "My soil has the following parameters: Nitrogen (N): %.2f, Phosphorus (P): %.2f, " +
                        "Potassium (K): %.2f, and pH level: %.2f. " +
                        "Based strictly on this data, provide a short, highly professional recommendation (under 100 words) " +
                        "stating the top 2 most profitable crops to grow and a quick fertilizer suggestion.",
                request.getRegion(), request.getNitrogen(), request.getPhosphorus(),
                request.getPotassium(), request.getPhLevel()
        );

        // 2. We format the request exactly how the Gemini API expects it
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        // 3. We use RestTemplate to fire the HTTP POST request to Google's servers
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Send the request and get the raw JSON response
            Map<String, Object> response = restTemplate.postForObject(
                    GEMINI_API_URL + geminiApiKey,
                    entity,
                    Map.class
            );

            // 4. Navigate through Google's nested JSON response to extract the actual AI text
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            System.err.println("Gemini API Error: " + e.getMessage());
            return "AI Analysis currently unavailable. Please check soil parameters manually.";
        }
    }
}