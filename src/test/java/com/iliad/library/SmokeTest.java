package com.iliad.library;

import com.iliad.library.controller.BookController;
import com.iliad.library.controller.ReviewController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class SmokeTest {

    @Autowired
    private BookController bookController;

    @Autowired
    private ReviewController reviewController;

    @Test
    void contextLoads() {
    }

    @Test
    void contextLoadsBook() throws Exception {
        assertThat(bookController).isNotNull();
    }

    @Test
    void contextLoadsReview() throws Exception {
        assertThat(reviewController).isNotNull();
    }
}
