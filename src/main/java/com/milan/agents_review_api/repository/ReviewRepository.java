package com.milan.agents_review_api.repository;

import com.milan.agents_review_api.models.Agent;
import com.milan.agents_review_api.models.Review;
import com.milan.agents_review_api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByAgentId(int agentId);

    Optional<Review> findByUserAndAgent(User user, Agent agent);

}
