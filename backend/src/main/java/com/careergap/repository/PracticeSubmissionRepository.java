package com.careergap.repository;

import com.careergap.entity.LearningModule;
import com.careergap.entity.PracticeSubmission;
import com.careergap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeSubmissionRepository extends JpaRepository<PracticeSubmission, Long> {
    List<PracticeSubmission> findByUserAndModuleOrderBySubmittedAtDesc(User user, LearningModule module);
    List<PracticeSubmission> findByUserIdAndModuleId(Long userId, Long moduleId);
    long countByUser(User user);
}

