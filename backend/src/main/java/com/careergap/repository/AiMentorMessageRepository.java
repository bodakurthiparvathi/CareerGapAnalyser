package com.careergap.repository;

import com.careergap.entity.AiMentorMessage;
import com.careergap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiMentorMessageRepository extends JpaRepository<AiMentorMessage, Long> {
    List<AiMentorMessage> findByUserOrderByCreatedAtAsc(User user);
    List<AiMentorMessage> findTop30ByUserOrderByCreatedAtAsc(User user);
}

