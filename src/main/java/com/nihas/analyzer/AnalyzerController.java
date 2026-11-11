package com.nihas.analyzer;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalyzerController {

    private final AnalyzerService service;
    private final RoleRepository roles;
    private final AnalysisRepository history;
    private final AiService ai;

    public AnalyzerController(AnalyzerService service, RoleRepository roles,
                              AnalysisRepository history, AiService ai) {
        this.service = service;
        this.roles = roles;
        this.history = history;
        this.ai = ai;
    }

    /** Role names for the dropdown (public). */
    @GetMapping("/roles")
    public List<String> roles() {
        return roles.findAll().stream().map(Role::getName).toList();
    }

    /** Analyze pasted resume text. */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody Map<String, String> body, Authentication auth) {
        String text = body.getOrDefault("text", "");
        String role = body.getOrDefault("role", "");
        if (text.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Resume text is empty."));
        }
        return ResponseEntity.ok(service.analyze(text, role, auth.getName()));
    }

    /** Analyze an uploaded PDF resume. */
    @PostMapping("/analyze-pdf")
    public ResponseEntity<?> analyzePdf(@RequestParam("file") MultipartFile file,
                                        @RequestParam("role") String role,
                                        Authentication auth) {
        try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
            String text = new PDFTextStripper().getText(doc);
            if (text.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Could not read text from this PDF."));
            }
            return ResponseEntity.ok(service.analyze(text, role, auth.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid PDF file."));
        }
    }

    /** Rewrite one weak bullet (AI if configured, template otherwise). */
    @PostMapping("/improve")
    public ResponseEntity<?> improve(@RequestBody Map<String, String> body) {
        String bullet = body.getOrDefault("bullet", "");
        String role = body.getOrDefault("role", "Software Developer");
        if (bullet.isBlank() || bullet.length() > 500) {
            return ResponseEntity.badRequest().body(Map.of("error", "Send a bullet under 500 characters."));
        }
        return ResponseEntity.ok(ai.improve(bullet, role));
    }

    /** The logged-in user's past analyses. */
    @GetMapping("/history")
    public List<Analysis> history(Authentication auth) {
        return history.findByUserEmailOrderByCreatedAtDesc(auth.getName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
