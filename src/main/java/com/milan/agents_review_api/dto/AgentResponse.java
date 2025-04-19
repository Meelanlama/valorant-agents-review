package com.milan.agents_review_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentResponse {
    private List<AgentDto> agents;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private Boolean isFirst;
    private Boolean isLast;
}
