package com.milan.agents_review_api.controller;

import com.milan.agents_review_api.controllers.AgentController;
import com.milan.agents_review_api.dto.AgentResponse;
import com.milan.agents_review_api.service.AgentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgentController.class)
public class AgentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private AgentService agentService;

    private AgentResponse agentResponse;

    @Test
    void contextLoads() {
        // Basic test to ensure context loads
    }


//    @BeforeEach
//    void setUp() {
//        agentResponse = new AgentResponse(); // Populate as needed
//    }
//
//    @Test
//    void getAllAgents() throws Exception {
//        when(agentService.getAllAgents(0, 10)).thenReturn(agentResponse);
//        mockMvc.perform(get("/api/agents/all")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }

}
