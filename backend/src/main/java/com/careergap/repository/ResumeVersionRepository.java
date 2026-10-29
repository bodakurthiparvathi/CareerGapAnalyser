package com.careergap.repository;

import com.careergap.entity.ResumeVersion;
import com.careergap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeVersionRepository extends JpaRepository<ResumeVersion, Long> {
    List<ResumeVersion> findByUserOrderByVersionNumberDesc(User user);
    Optional<ResumeVersion> findTopByUserOrderByVersionNumberDesc(User user);
    Optional<ResumeVersion> findByIdAndUser(Long id, User user);
}

