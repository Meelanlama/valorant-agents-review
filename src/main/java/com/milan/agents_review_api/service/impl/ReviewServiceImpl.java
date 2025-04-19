package com.milan.agents_review_api.service.impl;

import com.milan.agents_review_api.dto.ReviewDto;
import com.milan.agents_review_api.exceptions.AgentNotFoundException;
import com.milan.agents_review_api.exceptions.ReviewNotFoundException;
import com.milan.agents_review_api.models.Agent;
import com.milan.agents_review_api.models.Review;
import com.milan.agents_review_api.models.User;
import com.milan.agents_review_api.repository.AgentRepository;
import com.milan.agents_review_api.repository.ReviewRepository;
import com.milan.agents_review_api.service.ReviewService;
import com.milan.agents_review_api.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;

    private final ModelMapper mapper;

    private final AgentRepository agentRepo;

    @Override
    public ReviewDto createOrUpdateReview(int agentId, ReviewDto reviewDto) throws Exception {
        Agent agent = agentRepo.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with ID: " + agentId));

        User currentUser = CommonUtil.getLoggedInUser();

        if (reviewDto.getId() == null) {
            // ---- Create ----
            // Check if user already has a review for this agent
            Optional<Review> existingReviewOpt = reviewRepo.findByUserAndAgent(currentUser, agent);
            if (existingReviewOpt.isPresent()) {
                throw new IllegalStateException("You have already reviewed this agent.");
            }

            // if not reviewed and id is null,create new review
            Review review = mapper.map(reviewDto, Review.class);
            review.setAgent(agent);
            review.setUser(currentUser);

            Review saved = reviewRepo.save(review);
            return mapper.map(saved, ReviewDto.class);
        } else {
            // ---- Update ----
            Review review = reviewRepo.findById(reviewDto.getId())
                    .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + reviewDto.getId()));

            // Only allow update if this review belongs to the user and correct agent
            if (!review.getUser().getId().equals(currentUser.getId()) ||
                    !review.getAgent().getId().equals(agentId)) {
                throw new IllegalStateException("You are not allowed to update this review.");
            }

            review.setTitle(reviewDto.getTitle());
            review.setComment(reviewDto.getComment());
            review.setStars(reviewDto.getStars());

            Review saved = reviewRepo.save(review);
            return mapper.map(saved, ReviewDto.class);
        }
    }

    @Override
    public List<ReviewDto> getReviewsByAgentsId(int agentId){
       List<Review> reviews = reviewRepo.findByAgentId(agentId);

        //convert to dto
        return reviews.stream().map(review -> mapper.map(review, ReviewDto.class)).collect(Collectors.toList());
    }

    // Get review by ID and ensure it belongs to the specified agent
    @Override
    public ReviewDto getReviewById(int reviewId, int agentId) {
        // Find agent or throw if not found
        agentRepo.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found: " + agentId));

        // Find review or throw if not found
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found: " + reviewId));

        // Check if review is linked to the agent
        if (!review.getAgent().getId().equals(agentId)) {
            throw new ReviewNotFoundException("Review does not belong to the agent");
        }

        // Map entity to DTO and return
        return mapper.map(review, ReviewDto.class);
    }

    // Delete a review only if it belongs to the given agent
    @Override
    public void deleteReview(int agentId, int reviewId) {

        agentRepo.findById(agentId).orElseThrow(() -> new AgentNotFoundException("Agent not found: " + agentId));

        // Fetch review
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found: " + reviewId));

        // Ensure review belongs to the agent
        if (!review.getAgent().getId().equals(agentId)) {
            throw new ReviewNotFoundException("Review does not belong to the agent");
        }

        reviewRepo.delete(review);
    }

}
