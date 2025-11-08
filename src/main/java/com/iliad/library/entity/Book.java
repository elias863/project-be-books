package com.iliad.library.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "books")
@Data
public class Book {

    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Person> authors;

    @Column(columnDefinition = "LONGTEXT")
    private List<String> summaries;

    private List<String> editors;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Person> translators;

    private List<String> subjects;
    private List<String> bookshelves;
    private List<String> languages;
    private Boolean copyright;

    private String mediaType;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Format formats;

    private int downloadCount;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
}
