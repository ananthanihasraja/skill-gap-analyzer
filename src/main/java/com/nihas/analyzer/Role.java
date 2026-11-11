package com.nihas.analyzer;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A job role (e.g. "Java Developer") and the skills it needs.
 * Each skill can have aliases separated by "|", e.g. "spring boot|springboot".
 */
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> skills = new ArrayList<>();

    public Role() {}

    public Role(String name, List<String> skills) {
        this.name = name;
        this.skills = skills;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public List<String> getSkills() { return skills; }
    public void setName(String name) { this.name = name; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}
