package com.nihas.analyzer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/** Seeds the roles table on first run. Add more roles here anytime. */
@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roles;

    public DataLoader(RoleRepository roles) {
        this.roles = roles;
    }

    @Override
    public void run(String... args) {
        if (roles.count() > 0) return;

        roles.save(new Role("Java Developer", List.of(
                "java", "spring boot|springboot", "rest|restful", "mysql|sql",
                "hibernate|jpa", "maven|gradle", "git", "data structures|dsa",
                "oop|object-oriented", "junit|testing", "docker", "microservices",
                "linux", "aws|azure|gcp|cloud", "deployment|deployed")));

        roles.save(new Role("Web Developer", List.of(
                "html", "css", "javascript", "react|angular|vue", "node|nodejs",
                "rest|restful|api", "git", "responsive", "typescript",
                "mongodb|mysql|sql", "tailwind|bootstrap", "deployment|deployed|netlify|vercel")));

        roles.save(new Role("Backend Developer", List.of(
                "java|python|node|go", "rest|restful", "sql|mysql|postgresql",
                "spring boot|django|express|flask", "docker", "git", "jwt|authentication",
                "redis|caching", "microservices", "linux", "aws|azure|gcp|cloud",
                "data structures|dsa", "system design|system architecture")));

        roles.save(new Role("Data Analyst", List.of(
                "python", "sql|mysql", "excel", "pandas", "numpy", "power bi|tableau",
                "statistics", "data visualization|matplotlib", "data cleaning", "git")));
    }
}
