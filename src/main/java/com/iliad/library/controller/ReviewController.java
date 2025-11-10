package com.iliad.library.controller;

import com.iliad.library.dto.ReviewDTO;
import com.iliad.library.mapper.ReviewMapper;
import com.iliad.library.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@AllArgsConstructor // implementa il costruttore con tutti i campi
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO review) throws Exception {
        return ResponseEntity.status(200).body(reviewService.createReview(review)); // ------------->>>>>>> cambiare i valori di output
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReview(@RequestParam Long id) throws Exception {
        ReviewDTO review = reviewService.getReview(id);

        // se la rewiew è in PENDING restituisco 202
        if(review.getStatus().equals("PENDING"))
            return ResponseEntity.status(202).body(review);

        // se è in COMPLETED resituisco 200
        if(review.getStatus().equals("COMPLETED"))
            return ResponseEntity.status(200).body(review);
        else
            return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) throws Exception {
        reviewService.deleteReview(id);
        return ResponseEntity.status(200).build();
    }

    @PutMapping()
    public ResponseEntity<?> updateReview(@RequestBody ReviewDTO review) throws Exception {
        reviewService.updateReview(reviewMapper.toEntity(review));
        return ResponseEntity.status(200).build();
    }
}