package com.iliad.library.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "review")
@Data
public class Review {

    @Id
    //@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    //private Long bookId;

    private String review;
    private int score;
    private String status;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}