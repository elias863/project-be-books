package com.iliad.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.iliad.library.dto.BookDTO;
import com.iliad.library.dto.BookDTOReview;
import com.iliad.library.dto.BookDTOSearch;
import com.iliad.library.dto.ReviewDTO;
import com.iliad.library.entity.Book;
import com.iliad.library.entity.Format;
import com.iliad.library.entity.Review;
import com.iliad.library.mapper.BookMapper;
import com.iliad.library.mapper.FormatMapper;
import com.iliad.library.mapper.ReviewMapper;
import com.iliad.library.service.BookService;
import com.iliad.library.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private FormatMapper formatMapper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test   // Testa il metodo di ricerca dei libri
    void bookSearchHttpTest() throws Exception {

        String jsonResponse = this.restTemplate.getForObject("http://localhost:" + port + "/book/search?q=test",
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        List<BookDTOSearch> resultsDTO  = objectMapper.readValue(jsonResponse, List.class);

        assertThat(!resultsDTO.isEmpty());
    }

    @Test   // Testa il metodo per la creazione di una Review
            // per semplicità testo il caso della creazione di una Review che ha il libro associato già presente sul database
    public void createReviewHttpTestOk() throws Exception {

        // Mock chiamata a formatMapper
        Format format = new Format();
        format.setTextHtml("textHtml");

        when(formatMapper.toEntity(any())).thenReturn(format);

        // Mock chiamata a bookMapper
        Book book = new Book();
        book.setTitle("Simple title");

        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("test");

        when(bookMapper.toEntity(any())).thenReturn(book);

        // Mock chiamata a bookService (iniettata in reviewService
        var mockReview = new ReviewDTO();
        mockReview.setId(84l);
        mockReview.setReview("This is a Reviewblabllabllabllabllabllabllabllabllabllabllabllabllabl");
        mockReview.setScore(10);

        // converto a Oggetto a json
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(mockReview);

        when(bookService.getBookById(
                mockReview.getId())).thenReturn(book);

        // Mokko la chiamata a JdbcTemplate che ottiene la lista delle Review
        List<Review> reviewList = new ArrayList<>();
        Review review = new Review();
        review.setId(1l);
        reviewList.add(review);

        when(jdbcTemplate.query(anyString(),
                any(BeanPropertyRowMapper.class))).thenReturn(reviewList);

        // Mokko la chiamata a JdbcTemplate che ottiene l'id del libro
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class)))
                .thenReturn(1l);

        // Mokko la chiamata a JdbcTemplate che esegue l'inserimento della Review
        when(jdbcTemplate.update(anyString(),any(Long.class),any(String.class), any(Integer.class), any(String.class)
                )).thenReturn(1);

        // Mokko la chiamata a JdbcTemplate che restituisce l'id dell'ultimo record inserito
        when(jdbcTemplate.queryForObject(anyString(),eq(Long.class))).thenReturn(1l);

        // Mock chiamata a reviewService
        when(reviewService.createReview(
                mockReview)).thenReturn(mockReview);

        // Send course as body to /students/Student1/courses
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/review")
                .accept(MediaType.APPLICATION_JSON).content(json)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

    }

    @Test   // Testa il metodo per ottenere una review (GET) che va a buon fine
    void getReviewHttpTestOk() throws Exception {

        // come id impostare un id esistente nella tabella Review del DB
        String id = "2";

        ResponseEntity<BookDTOReview> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/review/"+id,
                BookDTOReview.class);

        assertThat(responseEntity.getStatusCode().equals(HttpStatus.OK));
    }

    @Test   // Testa il metodo per ottenere una Review (GET) che non va a buon fine
    void getReviewHttpTestBadRequest() throws Exception {

        // come id impostare un id NON esistente nella tabella Review del DB
        String id = "99999";

        ResponseEntity<BookDTOReview> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/review/"+id,
                BookDTOReview.class);

        assertThat(responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }

    @Test   // Testa il metodo per cancellare la review (DELETE) che va a buon fine
    void deleteReviewHttpTestOk() throws Exception {

        // come id impostare un id esistente nella tabella Review del DB
        Long id = 3l;

        mockMvc.perform(delete("/review/" + id))
                .andExpect(status().isOk());
    }

    @Test   // Testa il metodo per cancellare la review (DELETE) che non va a buon fine
    void deleteReviewHttpTestBadRequest() throws Exception {

        // come id impostare un id esistente nella tabella Review del DB
        String id = "99999";

        mockMvc.perform(delete("/review/" + id))
                .andExpect(status().is(204));
    }
}