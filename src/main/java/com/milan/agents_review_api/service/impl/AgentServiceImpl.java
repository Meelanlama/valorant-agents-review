package com.milan.agents_review_api.service.impl;

import com.milan.agents_review_api.dto.AgentDto;
import com.milan.agents_review_api.dto.AgentResponse;
import com.milan.agents_review_api.exceptions.AgentCreationException;
import com.milan.agents_review_api.exceptions.AgentNotFoundException;
import com.milan.agents_review_api.models.Agent;
import com.milan.agents_review_api.repository.AgentRepository;
import com.milan.agents_review_api.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepo;

    private final ModelMapper mapper;

    @Override
    public AgentDto createOrUpdateAgent(AgentDto agentDto) throws Exception {
        try {
            Agent agent;

            if (!ObjectUtils.isEmpty(agentDto.getId())) {
                // Update: fetch existing agent from DB
                agent = agentRepo.findById(agentDto.getId())
                        .orElseThrow(() -> new AgentNotFoundException("Agent not found with ID: " + agentDto.getId()));

                // Update agent fields from DTO
                mapper.map(agentDto, agent);
            } else {
                // Create: map DTO to new entity
                agent = mapper.map(agentDto, Agent.class);
            }

            Agent savedAgent = agentRepo.save(agent);
            return mapper.map(savedAgent, AgentDto.class);

        } catch (Exception e) {
            throw new AgentCreationException("Error while creating or updating agent", e);
        }
    }

    @Override
    public AgentResponse getAllAgents(int pageNo, int pageSize) throws Exception {

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        //get agents
        Page<Agent> agents = agentRepo.findAll(pageable);

        //get contents of agents
        List<Agent> listOfAgents = agents.getContent();

        //convert agents entity into agent dto
        List<AgentDto> agentDtos = listOfAgents.stream().map(a -> mapper.map(a,AgentDto.class)).collect(Collectors.toList());

       return AgentResponse.builder()
               .agents(agentDtos)
               .pageNo(pageNo)
               .pageSize(pageSize)
               .totalElements(agents.getTotalElements())
               .totalPages(agents.getTotalPages())
               .isFirst(agents.isFirst())
               .isLast(agents.isLast())
               .build();
    }

    @Override
    public AgentDto getAgentById(int id) throws Exception {
        Agent agent = agentRepo.findById(id).orElseThrow(() -> new AgentNotFoundException("Agent not found"));

        return mapper.map(agent, AgentDto.class);
    }

    @Override
    public void deleteAgentById(int id) throws Exception {
        Agent agent = agentRepo.findById(id).orElseThrow(() -> new AgentNotFoundException("Agent with id " + id + " not found"));
        agentRepo.delete(agent);
    }
}
