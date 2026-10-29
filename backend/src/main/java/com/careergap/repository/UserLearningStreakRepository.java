package com.careergap.repository;

import com.careergap.entity.User;
import com.careergap.entity.UserLearningStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLearningStreakRepository extends JpaRepository<UserLearningStreak, Long> {
    Optional<UserLearningStreak> findByUser(User user);
    Optional<UserLearningStreak> findByUserId(Long userId);
}

