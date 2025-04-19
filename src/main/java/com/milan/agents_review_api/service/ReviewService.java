package com.milan.agents_review_api.service;

import com.milan.agents_review_api.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto createOrUpdateReview(int agentId, ReviewDto reviewDto) throws Exception;

    List<ReviewDto> getReviewsByAgentsId(int agentId);

    ReviewDto getReviewById(int reviewId, int agentId);

    void deleteReview(int agentId, int reviewId);

}
