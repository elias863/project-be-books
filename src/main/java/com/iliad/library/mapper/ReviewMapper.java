package com.iliad.library.mapper;

import com.iliad.library.dto.ReviewDTO;
import com.iliad.library.entity.Review;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReviewMapper {

    private final BookMapper bookMapper;

    public Review toEntity(ReviewDTO dto){
        Review entity = new Review();
        //entity.setBookId(dto.getId());
        entity.setId(dto.getId());
        entity.setReview(dto.getReview());
        entity.setScore(dto.getScore());
//        entity.setBook(bookMapper.toEntity(dto.getBook()));
        return entity;
    }

    public ReviewDTO toDto(Review entity){
        ReviewDTO dto = new ReviewDTO();
        dto.setId(entity.getId());
        dto.setReview(entity.getReview());
        dto.setScore(entity.getScore());
        return dto;
    }
}
