package com.nihas.analyzer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Finds weak resume bullet points using simple rules. */
@Component
public class BulletChecker {

    private static final List<String> WEAK_STARTS = List.of(
            "worked on", "helped", "responsible for", "involved in", "assisted",
            "tried", "made a", "did ", "participated");

    public List<Map<String, String>> check(String resumeText) {
        List<Map<String, String>> issues = new ArrayList<>();

        for (String raw : resumeText.split("\\R")) {
            String line = raw.trim();
            if (!(line.startsWith("•") || line.startsWith("-") || line.startsWith("*"))) continue;

            String bullet = line.replaceFirst("^[•\\-*\\s]+", "").trim();
            if (bullet.length() < 20) continue;

            String lower = bullet.toLowerCase(Locale.ROOT);
            String problem = null;

            for (String weak : WEAK_STARTS) {
                if (lower.startsWith(weak)) {
                    problem = "Weak opening. Start with a strong verb like Built, Designed or Reduced.";
                    break;
                }
            }
            if (problem == null && !bullet.matches(".*\\d.*")) {
                problem = "No measurable result. Add a number (users, speed, size, time saved).";
            }

            if (problem != null) {
                Map<String, String> item = new LinkedHashMap<>();
                item.put("bullet", bullet);
                item.put("issue", problem);
                issues.add(item);
            }
            if (issues.size() >= 8) break;
        }
        return issues;
    }
}
