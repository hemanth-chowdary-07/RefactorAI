package com.refactorai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_history")
public class AnalysisHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String originalCode;

    @Column(columnDefinition = "TEXT")
    private String refactoredCode;

    @Column(columnDefinition = "TEXT")
    private String diff;

    @Column(name = "smells_count")
    private Integer smellsCount;

    @Column(columnDefinition = "TEXT")
    private String smellTypes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public AnalysisHistory() {
        this.createdAt = LocalDateTime.now();
    }

    public AnalysisHistory(User user, String originalCode, String refactoredCode,
                           String diff, Integer smellsCount, String smellTypes) {
        this.user = user;
        this.originalCode = originalCode;
        this.refactoredCode = refactoredCode;
        this.diff = diff;
        this.smellsCount = smellsCount;
        this.smellTypes = smellTypes;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }

    public String getRefactoredCode() {
        return refactoredCode;
    }

    public void setRefactoredCode(String refactoredCode) {
        this.refactoredCode = refactoredCode;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public Integer getSmellsCount() {
        return smellsCount;
    }

    public void setSmellsCount(Integer smellsCount) {
        this.smellsCount = smellsCount;
    }

    public String getSmellTypes() {
        return smellTypes;
    }

    public void setSmellTypes(String smellTypes) {
        this.smellTypes = smellTypes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}