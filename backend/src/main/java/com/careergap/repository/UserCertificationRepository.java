package com.careergap.repository;

import com.careergap.entity.User;
import com.careergap.entity.UserCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCertificationRepository extends JpaRepository<UserCertification, Long> {
    List<UserCertification> findByUser(User user);
    void deleteByUser(User user);
}
