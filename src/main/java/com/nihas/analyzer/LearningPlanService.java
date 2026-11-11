package com.nihas.analyzer;

import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Turns a list of missing skills into a week-by-week learning plan. */
@Component
public class LearningPlanService {

    // Rough days needed for the heavier skills. Everything else defaults to 3 days.
    private static final Map<String, Integer> DAYS = Map.ofEntries(
            Map.entry("spring boot", 10),
            Map.entry("data structures", 14),
            Map.entry("microservices", 7),
            Map.entry("docker", 5),
            Map.entry("aws", 7),
            Map.entry("react", 10),
            Map.entry("system design", 10),
            Map.entry("sql", 5),
            Map.entry("mysql", 5),
            Map.entry("rest", 4),
            Map.entry("hibernate", 5),
            Map.entry("statistics", 10)
    );

    public List<Map<String, Object>> buildPlan(List<String> missingSkills) {
        List<Map<String, Object>> plan = new ArrayList<>();
        int totalDays = 0;

        for (String skill : missingSkills) {
            int days = DAYS.getOrDefault(skill, 3);
            totalDays += days;

            Map<String, Object> step = new LinkedHashMap<>();
            step.put("skill", skill);
            step.put("days", days);
            step.put("week", (int) Math.ceil(totalDays / 7.0));
            step.put("link", "https://www.youtube.com/results?search_query="
                    + URLEncoder.encode(skill + " tutorial for beginners", StandardCharsets.UTF_8));
            plan.add(step);
        }
        return plan;
    }
}
