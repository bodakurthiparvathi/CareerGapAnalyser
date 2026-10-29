package com.careergap.repository;

import com.careergap.entity.User;
import com.careergap.entity.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    List<UserProject> findByUser(User user);
    void deleteByUser(User user);
}
