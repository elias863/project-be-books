package com.iliad.library.controller;

import com.iliad.library.dto.ReviewDTO;
import com.iliad.library.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@AllArgsConstructor // implementa il costruttore con tutti i campi
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewDTO createReview(@RequestBody ReviewDTO review) throws Exception {
        return reviewService.createReview(review);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ReviewDTO>> getReview(@RequestParam Long id) throws Exception {
        List<ReviewDTO> reviews = reviewService.getReview(id);

        // se la rewiew è in PENDING restituisco 202
        if(reviews.get(reviews.size()-1).getStatus().equals("PENDING"))
            return ResponseEntity.status(202).body(reviews);

        // se è in COMPLETED resituisco 200
        if(reviews.get(reviews.size()-1).getStatus().equals("COMPLETED"))
            return ResponseEntity.status(200).body(reviews);
        else
            return ResponseEntity.notFound().build();

    }
}