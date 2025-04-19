package com.milan.agents_review_api.controllers;

import com.milan.agents_review_api.dto.AgentDto;
import com.milan.agents_review_api.dto.AgentResponse;
import com.milan.agents_review_api.service.AgentService;
import com.milan.agents_review_api.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllAgents(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) throws Exception {

       AgentResponse agentResponse = agentService.getAllAgents(pageNo, pageSize);
       return CommonUtil.createBuildResponse(agentResponse, HttpStatus.OK);
    }

    @PostMapping("/create-update")
    public ResponseEntity<?> createUpdateAgent(@RequestBody AgentDto agentDto) throws Exception {

        HttpStatus status = (agentDto.getId() == null) ? HttpStatus.CREATED : HttpStatus.OK;

        AgentDto agents = agentService.createOrUpdateAgent(agentDto);
        return CommonUtil.createBuildResponse(agents, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAgentById(@PathVariable("id") int id) throws Exception {
        AgentDto agentDto = agentService.getAgentById(id);
        return CommonUtil.createBuildResponse(agentDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAgentById(@PathVariable("id") int id) throws Exception {
        agentService.deleteAgentById(id);
        return CommonUtil.createBuildResponseMessage("Agent deleted successfully", HttpStatus.OK);
    }

}
