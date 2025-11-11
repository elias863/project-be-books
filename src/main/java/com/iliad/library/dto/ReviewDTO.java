package com.iliad.library.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private Long bookId;
    private String review;
    private int score;
    private String status;
}