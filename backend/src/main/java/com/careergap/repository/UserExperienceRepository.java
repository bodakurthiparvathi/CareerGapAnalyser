package com.careergap.repository;

import com.careergap.entity.User;
import com.careergap.entity.UserExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserExperienceRepository extends JpaRepository<UserExperience, Long> {
    List<UserExperience> findByUser(User user);
    void deleteByUser(User user);
}
