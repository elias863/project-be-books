package com.iliad.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliad.library.dto.BookDTOReview;
import com.iliad.library.dto.BookDTOSearch;
import com.iliad.library.dto.ReviewDTO;
import com.iliad.library.service.BookService;
import com.iliad.library.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebMvcTest(ReviewController.class)
public class ControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void bookSearchHttpTest() throws Exception {

        String jsonResponse = this.restTemplate.getForObject("http://localhost:" + port + "/book/search?q=test",
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        List<BookDTOSearch> resultsDTO  = objectMapper.readValue(jsonResponse, List.class);

        assertThat(!resultsDTO.isEmpty());
    }

    @Test
    void createReviewHttpTest() throws Exception{

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReview("This is a Reviewblabllabllabllabllabllabllabllabllabllabllabllabllabl");
        reviewDTO.setScore(10);
        reviewDTO.setId(84l);

        ResponseEntity<ReviewDTO> responseEntity =
                restTemplate.postForEntity(
                        "http://localhost:"+port+"/review",
                        reviewDTO,
                        ReviewDTO.class
                );

        assert(responseEntity.getStatusCode().equals(HttpStatus.OK));
    }

    @Test
    void getReviewHttpTestOk() throws Exception {

        // come id impostare un id esistente nella tabella Review del DB
        String id = "2";

        ResponseEntity<BookDTOReview> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/review/"+id,
                BookDTOReview.class);

        assertThat(responseEntity.getStatusCode().equals(HttpStatus.OK));
    }

    @Test
    void getReviewHttpTestBadRequest() throws Exception {

        // come id impostare un id NON esistente nella tabella Review del DB
        String id = "999";

        ResponseEntity<BookDTOReview> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/review/"+id,
                BookDTOReview.class);

        assertThat(responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }

    @Test
    void deleteReviewHttpTestOk() throws Exception {

        // come id impostare un id esistente nella tabella Review del DB
        Long id = 5l;

        mockMvc.perform(delete("/review/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void deleteReviewHttpTestBadRequest() throws Exception {

        // come id impostare un id esistente nella tabella Review del DB
        Long id = 5l;

        mockMvc.perform(delete("/review/" + id))
                .andExpect(status().is(204));
    }
}