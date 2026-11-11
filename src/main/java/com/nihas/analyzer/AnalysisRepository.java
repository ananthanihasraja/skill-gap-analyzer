package com.nihas.analyzer;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    List<Analysis> findByUserEmailOrderByCreatedAtDesc(String userEmail);
}
