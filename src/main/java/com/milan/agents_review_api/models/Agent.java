package com.milan.agents_review_api.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String type;

    @OneToMany(mappedBy = "agent",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

}
