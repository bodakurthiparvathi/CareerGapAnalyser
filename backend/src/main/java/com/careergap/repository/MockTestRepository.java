package com.careergap.repository;

import com.careergap.entity.MockTest;
import com.careergap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockTestRepository extends JpaRepository<MockTest, Long> {
    List<MockTest> findByUserOrderByCreatedAtDesc(User user);
    List<MockTest> findByUserAndTargetRoleOrderByCreatedAtDesc(User user, String targetRole);
}

