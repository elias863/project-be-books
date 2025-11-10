package com.iliad.library.controller;

import com.iliad.library.dto.BookDTO;
import com.iliad.library.dto.BookDTOReview;
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
    public ResponseEntity<BookDTOReview> getReview(@PathVariable Long id){

        // review contiene la Review presa dal DB, se non esiste restituisce 404
        BookDTOReview bookWithReview;
        try{
            bookWithReview = reviewService.getReview(id);
        }
        catch (Exception e){
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.status(200).body(bookWithReview);
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