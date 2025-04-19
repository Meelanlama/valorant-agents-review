package com.milan.agents_review_api.repository;

import com.milan.agents_review_api.models.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgentRepository extends JpaRepository<Agent, Integer> {
    List<Agent> findByType(String type);

}
