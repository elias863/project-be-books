package com.iliad.library.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iliad.library.dto.BookDTO;
import com.iliad.library.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/book")
@AllArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookDTO> getBooks() throws JsonProcessingException {
        return bookService.getBooks();
    }
}