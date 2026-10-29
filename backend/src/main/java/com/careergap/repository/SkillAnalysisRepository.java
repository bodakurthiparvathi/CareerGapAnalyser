package com.careergap.repository;

import com.careergap.entity.SkillAnalysis;
import com.careergap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillAnalysisRepository extends JpaRepository<SkillAnalysis, Long> {
    List<SkillAnalysis> findByUserOrderByCreatedAtDesc(User user);
    long countByUser(User user);
}
