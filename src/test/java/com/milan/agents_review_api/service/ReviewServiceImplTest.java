package com.milan.agents_review_api.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.milan.agents_review_api.dto.AgentDto;
import com.milan.agents_review_api.dto.ReviewDto;
import com.milan.agents_review_api.exceptions.AgentNotFoundException;
import com.milan.agents_review_api.exceptions.ReviewNotFoundException;
import com.milan.agents_review_api.models.Agent;
import com.milan.agents_review_api.models.Review;
import com.milan.agents_review_api.models.User;
import com.milan.agents_review_api.repository.AgentRepository;
import com.milan.agents_review_api.repository.ReviewRepository;
import com.milan.agents_review_api.service.impl.ReviewServiceImpl;
import com.milan.agents_review_api.util.CommonUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Optional;

public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private ModelMapper modelMapper;

    private ReviewServiceImpl reviewService;

    private Agent agent;
    private Review review;
    private ReviewDto reviewDto;
    private AgentDto agentDto;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(reviewRepository, modelMapper, agentRepository);
        agent = Agent.builder().name("Sova").type("Initiator").build();
        agentDto = AgentDto.builder().name("Sova").type("Initiator").build();
        review = Review.builder().title("Nice Reveal").comment("Can clear site and initiate good").stars(5).build();
        reviewDto = ReviewDto.builder().title("Nice Reveal").comment("Can clear site and initiate good").stars(5).build();
    }


    @Test
    void shouldCreateReview_WhenAgentExistsAndUserNotReviewed() {
        try (MockedStatic<CommonUtil> mockedUtil = Mockito.mockStatic(CommonUtil.class)) {
            // 1. Mocks
            Agent agent = new Agent();
            agent.setId(1);

            User user = new User();
            user.setId(1);

            ReviewDto reviewDto = new ReviewDto();
            reviewDto.setTitle("Test Title");
            reviewDto.setComment("Test Comment");
            reviewDto.setStars(4);

            Review mappedReview = Review.builder()
                    .title(reviewDto.getTitle())
                    .comment(reviewDto.getComment())
                    .stars(reviewDto.getStars())
                    .agent(agent)
                    .user(user)
                    .build();

            Review savedReview = Review.builder()
                    .id(1)
                    .title("Test Title")
                    .comment("Test Comment")
                    .stars(4)
                    .agent(agent)
                    .user(user)
                    .build();

            ReviewDto savedDto = new ReviewDto();
            savedDto.setId(1);
            savedDto.setTitle("Test Title");
            savedDto.setComment("Test Comment");
            savedDto.setStars(4);

            // 2. Stubbing
            mockedUtil.when(CommonUtil::getLoggedInUser).thenReturn(user);
            Mockito.when(agentRepository.findById(1)).thenReturn(Optional.of(agent));
            Mockito.when(reviewRepository.findByUserAndAgent(user, agent)).thenReturn(Optional.empty());
            Mockito.when(modelMapper.map(reviewDto, Review.class)).thenReturn(mappedReview);
            Mockito.when(reviewRepository.save(mappedReview)).thenReturn(savedReview);
            Mockito.when(modelMapper.map(savedReview, ReviewDto.class)).thenReturn(savedDto);

            // 3. Execute
            ReviewDto result = reviewService.createOrUpdateReview(1, reviewDto);

            // 4. Verify
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getId()).isEqualTo(1);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void shouldThrowException_WhenUserTriesToReviewSameAgentTwice() {
        // Arrange
        Agent agent = Agent.builder().id(1).name("Phoenix").build();
        User user = User.builder().id(1).userName("milan").build();
        ReviewDto reviewDto = ReviewDto.builder().title("Great agent").comment("Good with flashes").stars(5).build();

        // Mock CommonUtil to simulate logged-in user
        try (MockedStatic<CommonUtil> mockedCommonUtil = mockStatic(CommonUtil.class)) {
            mockedCommonUtil.when(CommonUtil::getLoggedInUser).thenReturn(user);

            // Mock repository behavior
            when(agentRepository.findById(1)).thenReturn(Optional.of(agent));
            when(reviewRepository.findByUserAndAgent(user, agent)).thenReturn(Optional.of(new Review())); // existing review

            // Act & Assert
            assertThatThrownBy(() -> reviewService.createOrUpdateReview(1, reviewDto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("You have already reviewed this agent.");
        }
    }

    @Test
    void shouldUpdateReview_WhenReviewExistsAndBelongsToUser() {
        // Arrange
        Agent agent = Agent.builder().id(1).name("Phoenix").build();
        User user = User.builder().id(1).userName("milan").build();
        Review existingReview = Review.builder().id(1).title("Good agent").comment("Nice flashes").stars(4).agent(agent).user(user).build();
        ReviewDto reviewDto = ReviewDto.builder().id(1).title("Updated Review").comment("Very useful").stars(5).build();

        // Mock CommonUtil to simulate logged-in user
        try (MockedStatic<CommonUtil> mockedCommonUtil = mockStatic(CommonUtil.class)) {
            mockedCommonUtil.when(CommonUtil::getLoggedInUser).thenReturn(user);

            // Mock repository behavior
            when(agentRepository.findById(1)).thenReturn(Optional.of(agent));
            when(reviewRepository.findById(1)).thenReturn(Optional.of(existingReview));

            Review updatedReview = Review.builder().id(1).title("Updated Review").comment("Very useful").stars(5).agent(agent).user(user).build();
            when(modelMapper.map(reviewDto, Review.class)).thenReturn(updatedReview);
            when(reviewRepository.save(updatedReview)).thenReturn(updatedReview);

            // Act
            ReviewDto savedReviewDto = reviewService.createOrUpdateReview(1, reviewDto);

            // Assert
            assertThat(savedReviewDto).isNotNull();
            assertThat(savedReviewDto.getTitle()).isEqualTo("Updated Review");
            verify(reviewRepository, times(1)).save(updatedReview);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldThrowException_WhenUserTriesToUpdateAnotherUserReview() {
        // Arrange
        Agent agent = Agent.builder().id(1).name("Phoenix").build();
        User user = User.builder().id(1).userName("milan").build();
        User otherUser = User.builder().id(2).userName("john").build();
        Review existingReview = Review.builder().id(1).title("Good agent").comment("Nice flashes").stars(4).agent(agent).user(user).build();
        ReviewDto reviewDto = ReviewDto.builder().id(1).title("Updated Review").comment("Very useful").stars(5).build();

        // Mock CommonUtil to simulate logged-in user
        try (MockedStatic<CommonUtil> mockedCommonUtil = mockStatic(CommonUtil.class)) {
            mockedCommonUtil.when(CommonUtil::getLoggedInUser).thenReturn(otherUser);

            // Mock repository behavior
            when(agentRepository.findById(1)).thenReturn(Optional.of(agent));
            when(reviewRepository.findById(1)).thenReturn(Optional.of(existingReview));

            // Act & Assert
            assertThatThrownBy(() -> reviewService.createOrUpdateReview(1, reviewDto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("You are not allowed to update this review.");
        }
    }

    @Test
    public void ReviewService_DeleteReviewById_ReturnVoid() {
        int agentId = 1;
        int reviewId = 1;

        // Setup stubs
        when(agentRepository.findById(agentId)).thenReturn(Optional.of(agent));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // Call method
        assertAll(() -> reviewService.deleteReview(agentId, reviewId));

        // Verify deletion
        verify(reviewRepository).delete(review);
    }

}

