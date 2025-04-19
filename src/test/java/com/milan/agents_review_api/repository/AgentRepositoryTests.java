package com.milan.agents_review_api.repository;

import com.milan.agents_review_api.models.Agent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AgentRepositoryTests {

    @Autowired
    private AgentRepository agentRepo;

    @Test
    public void AgentRepository_SaveAll_ReturnSavedAgents() {

        //Arrange
        Agent agent = Agent.builder()
                .name("Jett").type("Initiator").build();

        //Act
        Agent savedAgent = agentRepo.save(agent);

        //Assert
        Assertions.assertThat(savedAgent.getId()).isNotNull();
        Assertions.assertThat(savedAgent.getName()).isEqualTo("Jett");
        Assertions.assertThat(savedAgent.getType()).isEqualTo("Initiator");
        Assertions.assertThat(savedAgent.getId()).isGreaterThan(0);
    }

    @Test
    public void AgentRepository_GetAll_ReturnMoreThenOneAgent() {
        //Arrange
        Agent agent = Agent.builder()
                .name("Jett").type("Duelist").build();

        Agent agent1 = Agent.builder()
                .name("Raze").type("Duelist").build();

        //Act
         agentRepo.save(agent);
         agentRepo.save(agent1);

        List<Agent> agentsList = agentRepo.findAll();

        //assert
        Assertions.assertThat(agentsList).isNotNull();
        Assertions.assertThat(agentsList).isNotEmpty();
        Assertions.assertThat(agentsList.size()).isEqualTo(2);
        Assertions.assertThat(agentsList)
                .extracting(Agent::getName)
                .containsExactlyInAnyOrder("Jett", "Raze");
    }

    @Test
    public void AgentRepository_FindById_ReturnAgent() {
        //Arrange
        Agent agent = Agent.builder()
                .name("Jett").type("Duelist").build();

        //Act
        Agent savedAgent = agentRepo.save(agent);

        Agent foundAgent = agentRepo.findById(savedAgent.getId()).get();

        //Assert
        Assertions.assertThat(foundAgent).isNotNull();
        Assertions.assertThat(foundAgent.getId()).isEqualTo(savedAgent.getId());
        Assertions.assertThat(foundAgent.getName()).isEqualTo("Jett");
    }

    @Test
    public void AgentRepository_FindByType_ReturnAgentNotNull() {
        //Arrange
        Agent agent = Agent.builder()
                .name("Jett").type("Duelist").build();

        //Act
        agentRepo.save(agent);

        List<Agent> foundAgents = agentRepo.findByType("Duelist");

        //Assert
        Assertions.assertThat(foundAgents).isNotNull();
        Assertions.assertThat(foundAgents).isNotEmpty();
        Assertions.assertThat(foundAgents.get(0).getType()).isEqualTo("Duelist");
    }

    @Test
    public void AgentRepository_UpdateAgent_ReturnAgentNotNull() {
        //Arrange
        Agent agent = Agent.builder()
                .name("Jett").type("Duelist").build();

        agentRepo.save(agent);

        Agent existingAgent = agentRepo.findById(agent.getId()).get();
        existingAgent.setName("Sova");
        existingAgent.setType("Initiator");

        Agent updatedAgent = agentRepo.save(existingAgent);

        Assertions.assertThat(updatedAgent).isNotNull();
        Assertions.assertThat(updatedAgent.getId()).isEqualTo(agent.getId());
        Assertions.assertThat(updatedAgent.getName()).isEqualTo("Sova");
        Assertions.assertThat(updatedAgent.getType()).isEqualTo("Initiator");
        Assertions.assertThat(updatedAgent.getId()).isGreaterThan(0);
    }

    @Test
    public void AgentRepository_DeleteById_RemovesAgent() {
        // Arrange
        Agent agent = Agent.builder()
                .name("Jett")
                .type("Duelist")
                .build();

        Agent savedAgent = agentRepo.save(agent);

        // Act
        agentRepo.deleteById(savedAgent.getId());
        Optional<Agent> deletedAgent = agentRepo.findById(savedAgent.getId());

        // Assert
        Assertions.assertThat(deletedAgent).isEmpty();
    }

}