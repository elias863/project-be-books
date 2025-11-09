package com.iliad.library.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "Review")
@Data
public class Review {

    @Id
    private Long id;

    private Long bookId;
    private String review;
    private int score;
    private String status;
}