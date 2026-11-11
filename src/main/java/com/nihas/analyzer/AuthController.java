package com.nihas.analyzer;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name = body.getOrDefault("name", "").trim();
        String email = body.getOrDefault("email", "").trim().toLowerCase();
        String password = body.getOrDefault("password", "");

        if (name.isEmpty() || !email.contains("@") || password.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Enter a name, a valid email and a password of 6+ characters."));
        }
        if (users.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered."));
        }
        users.save(new AppUser(name, email, encoder.encode(password)));
        return ResponseEntity.ok(Map.of("token", jwt.createToken(email), "name", name));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "").trim().toLowerCase();
        String password = body.getOrDefault("password", "");

        return users.findByEmail(email)
                .filter(u -> encoder.matches(password, u.getPasswordHash()))
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(
                        Map.of("token", jwt.createToken(email), "name", u.getName())))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Wrong email or password.")));
    }
}
