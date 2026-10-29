package com.careergap.repository;

import com.careergap.entity.SkillVerification;
import com.careergap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillVerificationRepository extends JpaRepository<SkillVerification, Long> {
    List<SkillVerification> findByUserOrderByCreatedAtDesc(User user);
    Optional<SkillVerification> findByUserAndSkillNameIgnoreCase(User user, String skillName);
    Optional<SkillVerification> findByUserIdAndSkillNameIgnoreCase(Long userId, String skillName);
    long countByUserAndStatus(User user, String status);
}

