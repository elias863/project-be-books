package com.iliad.library.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliad.library.dto.BookDTO;
import com.iliad.library.dto.ResultsDTO;
import com.iliad.library.entity.Book;
import com.iliad.library.mapper.BookMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@AllArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final String GUTENDEX_URL = "https://gutendex.com/books";

    public List<BookDTO> getBooks() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        String jsonResponse = restTemplate.getForObject(GUTENDEX_URL, String.class);

        ObjectMapper mapper = new ObjectMapper();
        ResultsDTO resultsDTO  = mapper.readValue(jsonResponse, ResultsDTO.class);

        return resultsDTO.getResults();
    }

    public Book getBookById(Long id) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        String jsonResponse = restTemplate.getForObject(GUTENDEX_URL+"/"+id, String.class);

        ObjectMapper mapper = new ObjectMapper();
        BookDTO bookDTO  = mapper.readValue(jsonResponse, BookDTO.class);

        if(bookDTO == null)
            throw new Exception("Nessun libro con id: "+id);

        return bookMapper.toEntity(bookDTO);
    }
}
