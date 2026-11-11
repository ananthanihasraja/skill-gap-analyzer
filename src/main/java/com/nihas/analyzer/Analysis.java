package com.nihas.analyzer;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** One saved analysis, belonging to a user. */
@Entity
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private String roleName;
    private int score;

    @Column(length = 1000)
    private String matched;

    @Column(length = 1000)
    private String missing;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Analysis() {}

    public Analysis(String userEmail, String roleName, int score, String matched, String missing) {
        this.userEmail = userEmail;
        this.roleName = roleName;
        this.score = score;
        this.matched = matched;
        this.missing = missing;
    }

    public Long getId() { return id; }
    public String getRoleName() { return roleName; }
    public int getScore() { return score; }
    public String getMatched() { return matched; }
    public String getMissing() { return missing; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
