package com.iliad.library.service;

import com.iliad.library.config.RabbitMQConfig;
import com.iliad.library.dto.ReviewDTO;
import com.iliad.library.entity.Book;
import com.iliad.library.entity.Review;
import com.iliad.library.mapper.ReviewMapper;
import com.iliad.library.repository.BookRepository;
import com.iliad.library.repository.DatabaseUpdate;
import com.iliad.library.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final BookService bookService;
    private final RabbitTemplate rabbitTemplate;
    private final BookRepository bookRepository;
    private final DatabaseUpdate databaseUpdate;

    public ReviewDTO createReview(Review review) throws Exception {

        // controllo che l'id matchi con l'API
        if(bookService.getBookById(review.getId()).getId()==null)
            throw new Exception("Non esiste un libro con id: "+review.getId());

        // controllo che la review sia di almeno 30 caratteri
        if(review.getReview().length() < 30)
            throw new Exception("La review deve essere di almeno 30 caratteri!");

        // controllo che lo score sia compreso tra 0 e 10
        if(review.getScore()<0 || review.getScore() > 10)
            throw new Exception("Lo score deve essere compreso tra 0 e 10!");

        // Setto lo status temporaneamente in PENDING
        Review saved = review;
        saved.setId(review.getId());
        saved.setReview(review.getReview());
        saved.setScore(review.getScore());
        saved.setStatus("PENDING"); // Setto temporaneamente lo stato su PENDING --> diventerà COMPLETED quando arricchirò i dati accodati con RabbitMQ

        // prelevo il libro della review (con tutti i dati per l'arricchimento)
        Book reviewBook = bookService.getBookById(review.getId());
        List<Review> bookReviewList = new ArrayList<>(0);
        bookReviewList.add(saved);
        reviewBook.setReviews(bookReviewList);

        // cambio la codifica della tabella Book perchè dava un errore: java.sql.SQLSyntaxErrorException: (conn=3) Incorrect string value: '\xAC\xED\x00\x05sr...' for column `mylibrary`.`books`.`summaries` at row 1
        // SOLUZIONE: https://stackoverflow.com/questions/2687164/mysql-utf-encoding
        Book b = new Book();
        b.setId(0l);
        bookRepository.save(b);    // salvo un libro per creare la tabella

        //bookRepository.changeEncoding();    // cambio la codifica
        databaseUpdate.alterMyTableAddMyColumn();

        // salvo il libro
        bookRepository.save(reviewBook);

        // salvo la review semplice
        reviewRepository.save(saved);  // salvo la review (senza l'arricchimento)

        // recupero la review salvata dal db (per "pulizia")
        saved = reviewRepository.getReferenceById(review.getId());
        saved.setBook(reviewBook); // aggiungo il riferimento al libro

        // invio la review arricchita alla coda di RabbitMQ
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, saved);

        return reviewMapper.toDto(review);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(Review review) {

        // imposto lo stato a COMPLETED
        review.setStatus("COMPLETED");
        // Salvo il libro inerente alla review attuale
        reviewRepository.save(review);
    }
}
