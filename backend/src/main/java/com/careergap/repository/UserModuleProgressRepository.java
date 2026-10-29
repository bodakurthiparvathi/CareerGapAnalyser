package com.careergap.repository;

import com.careergap.entity.LearningModule;
import com.careergap.entity.User;
import com.careergap.entity.UserModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserModuleProgressRepository extends JpaRepository<UserModuleProgress, Long> {
    List<UserModuleProgress> findByUser(User user);
    Optional<UserModuleProgress> findByUserAndModule(User user, LearningModule module);
    Optional<UserModuleProgress> findByUserIdAndModuleId(Long userId, Long moduleId);
    long countByUserAndStatus(User user, String status);
}

