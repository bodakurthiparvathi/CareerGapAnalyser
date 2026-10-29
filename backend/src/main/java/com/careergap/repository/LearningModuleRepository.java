package com.careergap.repository;

import com.careergap.entity.LearningModule;
import com.careergap.entity.LearningRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningModuleRepository extends JpaRepository<LearningModule, Long> {
    List<LearningModule> findByRoadmapOrderByOrderIndexAsc(LearningRoadmap roadmap);
    List<LearningModule> findByRoadmapIdOrderByOrderIndexAsc(Long roadmapId);
}

