package com.refactorai.repository;

import com.refactorai.entity.AnalysisHistory;
import com.refactorai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, Long> {
    List<AnalysisHistory> findByUserOrderByCreatedAtDesc(User user);
    List<AnalysisHistory> findTop10ByUserOrderByCreatedAtDesc(User user);
}