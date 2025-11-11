package com.iliad.library.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table(name = "Book")
@Data
public class Book {

    @Id
    private Long id;
    private int bookId;
    private String title;
    private List<Person> authors;
    private List<String> summaries;
    private List<Person> editors;
    private List<Person> translators;
    private List<String> subjects;
    private List<String> bookshelves;
    private List<String> languages;
    private Boolean copyright;
    private String mediaType;
    private Format formats;
    private int downloadCount;
    private List<Review> reviews;
}
