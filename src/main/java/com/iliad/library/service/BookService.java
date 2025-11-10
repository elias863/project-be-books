package com.iliad.library.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliad.library.dto.BookDTO;
import com.iliad.library.dto.BookDTOSearch;
import com.iliad.library.dto.ResultsDTO;
import com.iliad.library.entity.Book;
import com.iliad.library.mapper.BookMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final String GUTENDEX_URL = "https://gutendex.com/books";

    // Ottiene tutti i libri
    public List<BookDTO> getBooks() throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();

        String jsonResponse = restTemplate.getForObject(GUTENDEX_URL, String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        ResultsDTO resultsDTO  = objectMapper.readValue(jsonResponse, ResultsDTO.class);

        return resultsDTO.getResults();
    }

    // Ottiene tutti i libri filtrando per il loro contenuto (Autore, titolo ecc.)
    public List<BookDTOSearch> getBooksSearch(String query) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();

        String jsonResponse = restTemplate.getForObject(GUTENDEX_URL+"?search="+query, String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        ResultsDTO resultsDTO  = objectMapper.readValue(jsonResponse, ResultsDTO.class);

        // mappo i BookDTO nei BookDTOSearch (solo i campi che ha senso mostrare per l'output della ricerca)
        List<BookDTOSearch> dtoSearches = new ArrayList<>(0);
        for(BookDTO b:resultsDTO.getResults()){
            BookDTOSearch bookDTOSearch = new BookDTOSearch();
            bookDTOSearch.setId(b.getId());
            bookDTOSearch.setBookshelves(b.getBookshelves());
            bookDTOSearch.setFormats(b.getFormats());
            bookDTOSearch.setAuthors(b.getAuthors());
            bookDTOSearch.setEditors(b.getEditors());
            bookDTOSearch.setSubjects(b.getSubjects());
            bookDTOSearch.setTranslators(b.getTranslators());
            bookDTOSearch.setTitle(b.getTitle());
            bookDTOSearch.setSummaries(b.getSummaries());
            bookDTOSearch.setMediaType(b.getMediaType());
            dtoSearches.add(bookDTOSearch);
        }

        return dtoSearches;
    }

    // Ottengo il libro con id specificato
    public Book getBookById(Long id) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        String jsonResponse = restTemplate.getForObject(GUTENDEX_URL+"/"+id, String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        BookDTO bookDTO  = objectMapper.readValue(jsonResponse, BookDTO.class);

        // Se non trova nessun libro con quell'id restituisce un oggetto vuoto
        if(bookDTO == null)
            return new Book();

        return bookMapper.toEntity(bookDTO);
    }
}
