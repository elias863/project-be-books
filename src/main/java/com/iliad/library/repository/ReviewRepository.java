package com.iliad.library.repository;

import com.iliad.library.entity.Review;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends CrudRepository<Review,Long> {
}