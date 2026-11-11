package com.nihas.analyzer;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class AnalyzerService {

    private final RoleRepository roles;
    private final AnalysisRepository history;
    private final LearningPlanService planService;
    private final BulletChecker bulletChecker;

    public AnalyzerService(RoleRepository roles, AnalysisRepository history,
                           LearningPlanService planService, BulletChecker bulletChecker) {
        this.roles = roles;
        this.history = history;
        this.planService = planService;
        this.bulletChecker = bulletChecker;
    }

    public Map<String, Object> analyze(String resumeText, String roleName, String userEmail) {
        Role role = roles.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + roleName));

        String text = resumeText.toLowerCase(Locale.ROOT);

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String skill : role.getSkills()) {
            String label = skill.split("\\|")[0]; // first name is shown, the rest are aliases
            if (containsAny(text, skill.split("\\|"))) {
                matched.add(label);
            } else {
                missing.add(label);
            }
        }

        int score = role.getSkills().isEmpty() ? 0
                : (int) Math.round(100.0 * matched.size() / role.getSkills().size());

        history.save(new Analysis(userEmail, roleName, score,
                String.join(", ", matched), String.join(", ", missing)));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("role", roleName);
        result.put("score", score);
        result.put("matched", matched);
        result.put("missing", missing);
        result.put("tips", tips(text));
        result.put("plan", planService.buildPlan(missing));
        result.put("weakBullets", bulletChecker.check(resumeText));
        return result;
    }

    /** Whole-word match so "java" does not match "javascript". */
    private boolean containsAny(String text, String[] aliases) {
        for (String alias : aliases) {
            Pattern p = Pattern.compile("(?<![a-z0-9])" + Pattern.quote(alias.trim()) + "(?![a-z0-9])");
            if (p.matcher(text).find()) return true;
        }
        return false;
    }

    /** Simple rule-based resume advice. */
    private List<String> tips(String text) {
        List<String> tips = new ArrayList<>();
        if (!text.contains("github")) tips.add("Add your GitHub link.");
        if (!text.contains("linkedin")) tips.add("Add your LinkedIn link.");
        if (!text.contains("project")) tips.add("Add a Projects section.");
        if (!text.contains("intern")) tips.add("No internship found. Apply for one, even a small remote one.");
        if (text.contains("communication") || text.contains("decision making") || text.contains("time management")) {
            tips.add("Remove generic soft skills like Communication or Time Management. Show them through projects instead.");
        }
        if (text.length() < 800) tips.add("Resume looks short. Add more detail on projects.");
        return tips;
    }
}
