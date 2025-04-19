package com.milan.agents_review_api.service;

import com.milan.agents_review_api.dto.AgentDto;
import com.milan.agents_review_api.dto.AgentResponse;

public interface AgentService {

    AgentDto createOrUpdateAgent(AgentDto agentDto) throws Exception;

    AgentResponse getAllAgents(int pageNo,int pageSize) throws Exception;

    AgentDto getAgentById(int id) throws Exception;

    void deleteAgentById(int id) throws Exception;
}
