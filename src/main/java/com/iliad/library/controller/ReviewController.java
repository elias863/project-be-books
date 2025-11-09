package com.iliad.library.controller;

import com.iliad.library.dto.ReviewDTO;
import com.iliad.library.mapper.ReviewMapper;
import com.iliad.library.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
@AllArgsConstructor // implementa il costruttore con tutti i campi
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ReviewDTO createReview(@RequestBody ReviewDTO review) throws Exception {
        return reviewService.createReview(review);
    }
}