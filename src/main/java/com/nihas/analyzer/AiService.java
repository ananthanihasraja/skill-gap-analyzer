package com.nihas.analyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Rewrites a weak resume bullet. Uses the Claude API when ANTHROPIC_API_KEY is set,
 * otherwise falls back to a fill-in-the-blanks template.
 */
@Service
public class AiService {

    private final String apiKey;
    private final String model;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    public AiService(@Value("${anthropic.api-key}") String apiKey,
                     @Value("${anthropic.model}") String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    public Map<String, Object> improve(String bullet, String role) {
        if (apiKey == null || apiKey.isBlank()) {
            return Map.of("source", "template", "suggestions", List.of(
                    "Built [what] using [technology] that [does what], used by [N] users.",
                    "Reduced [problem] by [N]% by implementing [solution] with [technology]."));
        }

        try {
            String prompt = "Rewrite this resume bullet for a " + role + " candidate.\n"
                    + "Rules: start with a strong action verb, keep it truthful, do NOT invent numbers "
                    + "(use a placeholder like [N] where a metric is needed), max 25 words.\n"
                    + "Return exactly 2 alternatives, one per line, no numbering, no extra text.\n\n"
                    + "Bullet: " + bullet;

            String body = mapper.writeValueAsString(Map.of(
                    "model", model,
                    "max_tokens", 300,
                    "messages", List.of(Map.of("role", "user", "content", prompt))));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.anthropic.com/v1/messages"))
                    .timeout(Duration.ofSeconds(30))
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                return Map.of("source", "error", "suggestions", List.of("AI service error (" + res.statusCode() + ")."));
            }

            JsonNode root = mapper.readTree(res.body());
            String text = root.path("content").path(0).path("text").asText("");
            List<String> lines = text.lines().map(String::trim).filter(s -> !s.isEmpty()).toList();
            return Map.of("source", "ai", "suggestions", lines);

        } catch (Exception e) {
            return Map.of("source", "error", "suggestions", List.of("Could not reach the AI service."));
        }
    }
}
