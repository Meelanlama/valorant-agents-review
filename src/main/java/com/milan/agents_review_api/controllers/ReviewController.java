package com.milan.agents_review_api.controllers;

import com.milan.agents_review_api.dto.AgentDto;
import com.milan.agents_review_api.dto.ReviewDto;
import com.milan.agents_review_api.service.ReviewService;
import com.milan.agents_review_api.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/agents/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create-update/{agentId}")
    public ResponseEntity<?> createOrUpdateReview(@PathVariable("agentId") int agentId,
                                                  @RequestBody ReviewDto reviewDto) throws Exception {

        ReviewDto result = reviewService.createOrUpdateReview(agentId, reviewDto);
        HttpStatus status = (reviewDto.getId() == null) ? HttpStatus.CREATED : HttpStatus.OK;
        return CommonUtil.createBuildResponse(result, status);
    }

    //get all reviews of that agents
    @GetMapping("/getByAgentId/{agentId}")
    public ResponseEntity<?> getReviewsByAgentsId(@PathVariable(value = "agentId") int reviewId) {
        List<ReviewDto> reviewsByAgentsId = reviewService.getReviewsByAgentsId(reviewId);
        if(CollectionUtils.isEmpty(reviewsByAgentsId)) {
            return ResponseEntity.noContent().build();
        }
        return CommonUtil.createBuildResponse(reviewsByAgentsId,HttpStatus.OK);
    }

    //get only one review according to their review id and agent id
    @GetMapping("/{agentId}/{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable("agentId") int agentId,
                                           @PathVariable("reviewId") int reviewId) {

        ReviewDto result = reviewService.getReviewById(reviewId, agentId);

        if (ObjectUtils.isEmpty(result)) {
            return ResponseEntity.noContent().build();
        }

        return CommonUtil.createBuildResponse(result, HttpStatus.OK);
    }

    //delete agent
    @DeleteMapping("/{agentId}/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable("agentId") int agentId, @PathVariable("id") int reviewId) {
        reviewService.deleteReview(agentId, reviewId);
        return CommonUtil.createErrorResponseMessage("Review deleted successfully",HttpStatus.OK);
    }
}