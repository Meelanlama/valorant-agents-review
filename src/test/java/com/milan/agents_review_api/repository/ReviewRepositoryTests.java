package com.milan.agents_review_api.repository;

import com.milan.agents_review_api.models.Agent;
import com.milan.agents_review_api.models.Review;
import com.milan.agents_review_api.models.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private Agent createAgent(String name) {
        Agent agent = Agent.builder().name(name).type("Duelist").build();
        return entityManager.persist(agent);
    }

    private User createUser(String username) {
        User user = User.builder().userName(username).email(username + "@example.com").build();
        return entityManager.persist(user);
    }


    @Test
    public void shouldThrowException_WhenUserTriesToReviewSameAgentTwice() {
        Agent agent = createAgent("Omen");
        User user = createUser("milan");

        Review review1 = Review.builder()
                .title("Nice smokes")
                .comment("Good map control")
                .stars(4)
                .agent(agent)
                .user(user)
                .build();

        Review review2 = Review.builder()
                .title("Still good")
                .comment("Even better with teleport")
                .stars(5)
                .agent(agent)
                .user(user)
                .build();

        reviewRepository.save(review1);

        // Assert that saving another review for same user-agent throws exception
        Assertions.assertThatThrownBy(() -> reviewRepository.saveAndFlush(review2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
