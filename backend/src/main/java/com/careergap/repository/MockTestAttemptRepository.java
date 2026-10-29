package com.careergap.repository;

import com.careergap.entity.MockTestAttempt;
import com.careergap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockTestAttemptRepository extends JpaRepository<MockTestAttempt, Long> {
    List<MockTestAttempt> findByUserOrderByAttemptedAtDesc(User user);
    List<MockTestAttempt> findByMockTestIdOrderByAttemptedAtDesc(Long testId);
    long countByUser(User user);
}

