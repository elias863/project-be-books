package com.iliad.library.service;

import com.iliad.library.config.RabbitMQConfig;
import com.iliad.library.dto.BookDTO;
import com.iliad.library.dto.ReviewDTO;
import com.iliad.library.entity.Book;
import com.iliad.library.entity.Format;
import com.iliad.library.entity.Person;
import com.iliad.library.entity.Review;
import com.iliad.library.exception.NotExistingReviewException;
import com.iliad.library.mapper.BookMapper;
import com.iliad.library.mapper.ReviewMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final BookService bookService;
    private final RabbitTemplate rabbitTemplate;
    private static Long lastBookId = 0l;
    private final BookMapper bookMapper;
    private final JdbcTemplate jdbcTemplate;

    public ReviewDTO createReview(ReviewDTO reviewDTO) throws Exception {

        // prendo il libro a cui si riferisce la review
        Book reviewbook = bookService.getBookById(reviewDTO.getId());

        // controllo che l'id matchi con l'API
        if(reviewbook.getId()==null)
            throw new Exception("Non esiste un libro con id: "+reviewDTO.getId());

        // controllo che la review sia di almeno 30 caratteri
        if(reviewDTO.getReview().length() < 30)
            throw new Exception("La review deve essere di almeno 30 caratteri!");

        // controllo che lo score sia compreso tra 0 e 10
        if(reviewDTO.getScore()<0 || reviewDTO.getScore() > 10)
            throw new Exception("Lo score deve essere compreso tra 0 e 10!");

        // Creo la review "di base" (senza arricchimento)
        Review saved = new Review();
        saved.setBookId(reviewDTO.getId());
        saved.setReview(reviewDTO.getReview());
        saved.setScore(reviewDTO.getScore());
        saved.setStatus("PENDING"); // Setto temporaneamente lo stato su PENDING --> diventerà COMPLETED quando arricchirò i dati accodati con RabbitMQ

        // aggiungo la review al libro a cui si riferisce
        List<Review> newReviewList = null;
        if(reviewbook.getReviews() == null)
            newReviewList = new ArrayList<>(0);

        newReviewList.add(saved);

        reviewbook.setReviews(newReviewList);

        // Guardo sul db se esiste già il libro a cui voglio fare la recensione
        List<Review> reviews = new ArrayList<>(0);
        String sql = "SELECT * FROM Book,Review WHERE Book.id=Review.book_id AND Review.bookId = "+saved.getBookId();
        reviews = jdbcTemplate.query(sql,new BeanPropertyRowMapper(Review.class));

        // se il libro esiste già...
        if(!reviews.isEmpty()){ // aggiorno soltanto il campo STATUS (gli altri dati di arricchimento per questo libro sono già presenti)
            Long lastBookIdSaved = reviews.get(reviews.size()-1).getBookId();

            // inserisco soltanto la review con il riferimento (lastBookIdSaved) al libro già esistente
            sql = "INSERT INTO Review (bookId, review, score, status, book_id) VALUES (?,?,?,?,?)";
            jdbcTemplate.update(sql, saved.getBookId(), saved.getReview(), saved.getScore(), "COMPLETED",lastBookIdSaved);
        }
        else    // altrimenti salvo tutti i dati di arricchimento (Book ecc.) in modo asincrono con RabbitMQ
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, reviewbook);

        return reviewMapper.toDto(saved);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void reviewBookInsertReview(Book reviewbook) {

        // Creo il libro (necessario per creare la Review)
        String sql = "INSERT INTO Book (title, copyright, mediaType, " +
                "downloadCount) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, reviewbook.getTitle(), reviewbook.getCopyright(),
                reviewbook.getMediaType(), reviewbook.getDownloadCount()
        );

        // 0ttengo l'id del libro appena inserito
        lastBookId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        // Inserimento formats relativi al Book
        Format formats = reviewbook.getFormats();
        sql = "INSERT INTO Format (textHtml, applicationEpubZip, applicationMobiPocket" +
                ", textPlainAscii, textPlainUtf8, textHtmlCharsetUtf8, applicationRdfXml" +
                ", imageJpeg, applicationOctetStream, downloadCount, book_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql, formats.getTextHtml(), formats.getApplicationEpubZip(), formats.getApplicationMobiPocket(),
                formats.getTextPlainAscii(), formats.getTextPlainUtf8(), formats.getTextHtmlCharsetUtf8(),
                formats.getApplicationRdfXml(), formats.getImageJpeg(), formats.getApplicationOctetStream()
                ,formats.getDownloadCount(), lastBookId);

        // Inserimento authors
        List<Person> authors = reviewbook.getAuthors();
        for(Person a:authors){
            sql = "INSERT INTO Author (name, birthYear, deathYear) VALUES (?,?,?)";
            jdbcTemplate.update(sql,a.getName(), a.getBirthYear(), a.getDeathYear());
            Long lastAuthorId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            // Aggiungo i riferimenti nella tabella BookAuthor
            sql = "INSERT INTO BookAuthor (book_id, author_id) VALUES (?,?)";
            jdbcTemplate.update(sql, lastBookId, lastAuthorId);
        }

        // Inserimento summaries
        List<String> summaries = reviewbook.getSummaries();
        sql = "INSERT INTO Summary (summary, book_id) VALUES (?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String summary = summaries.get(i);
                ps.setString(1, summary);
                ps.setLong(2, lastBookId);          // --> DA SISTEMARE COME L'ALTRO
            }

            @Override
            public int getBatchSize() {
                return summaries.size();
            }
        });

        // Inserimento editors
        List<Person> editors = reviewbook.getEditors();
        for(Person e:editors){
            sql = "INSERT INTO Editor (editor) VALUES (?)";
            jdbcTemplate.update(sql,e);
            Long lastEditorId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            // Aggiungo i riferimenti nella tabella BookAuthor
            sql = "INSERT INTO BookEditor (book_id, editor_id) VALUES (?,?)";
            jdbcTemplate.update(sql, lastBookId, lastEditorId);
        }

        // Inserimento translators
        List<Person> translators = reviewbook.getTranslators();
        for(Person a:translators){
            sql = "INSERT INTO Translator (name, birthYear, deathYear) VALUES (?,?,?)";
            jdbcTemplate.update(sql,a.getName(), a.getBirthYear(), a.getDeathYear());
            Long lastAuthorId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            // Aggiungo i riferimenti nella tabella BookAuthor
            sql = "INSERT INTO BookTranslator (book_id, translator_id) VALUES (?,?)";
            jdbcTemplate.update(sql, lastBookId, lastAuthorId);
        }

        //  Inserimento subjects
        List<String> subjects = reviewbook.getSubjects();
        sql = "INSERT INTO Subject (subject, book_id) VALUES (?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String subject = subjects.get(i);
                ps.setString(1, subject);
                ps.setLong(2, lastBookId);
            }

            @Override
            public int getBatchSize() {
                return subjects.size();
            }
        });

        // Inserimento bookshelves
        List<String> bookshelves = reviewbook.getBookshelves();
        for(String b:bookshelves){
            sql = "INSERT INTO Bookshelf (bookshelf) VALUES (?)";
            jdbcTemplate.update(sql,b);
            Long lastBookShelfId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            // Aggiungo i riferimenti nella tabella BookAuthor
            sql = "INSERT INTO BookBookshelf (book_id, bookshelf_id) VALUES (?,?)";
            jdbcTemplate.update(sql, lastBookId, lastBookShelfId);
        }

        // Inserimento languages
        List<String> languages = reviewbook.getLanguages();
        for(String l:languages){
            sql = "INSERT INTO Language (language) VALUES (?)";
            jdbcTemplate.update(sql,l);
            Long lastLanguageId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            // Aggiungo i riferimenti nella tabella BookAuthor
            sql = "INSERT INTO BookLanguage (book_id, language_id) VALUES (?,?)";
            jdbcTemplate.update(sql, lastBookId, lastLanguageId);
        }

        // salvo la review con il campo status=COMPLETED
        Review lastReview = reviewbook.getReviews().get(reviewbook.getReviews().size()-1);
        sql = "INSERT INTO Review (bookId, review, score, status, book_id) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql, lastReview.getBookId(), lastReview.getReview(), lastReview.getScore(), "COMPLETED",lastBookId);
    }

    // Ottengo tutte le review di un dato BookId preso in input
    public BookDTO getReview(Long id) throws Exception {

        // Prelevo la Review (semplice)
        String sql = "SELECT * FROM Review WHERE id = "+id;
        Review fromDB = new Review();
        try{
            fromDB = (Review) jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{id},
                    new BeanPropertyRowMapper(Review.class));
        }
        catch (Exception e){
            throw new NotExistingReviewException();  // rimanda l'eccezione al controller (che la gestisce)
        }

        // L'arricchimento della review è uguale per tutte dato che si riferiscono allo stesso libro
        Book enrichedData = bookService.getBookById(id);

        // mappo a DTO tutte le Review prima di passarle al Controller
        List<Review> newReviewList = new ArrayList<>();
        newReviewList.add(fromDB);

        enrichedData.setReviews(newReviewList);

        return bookMapper.toDto(enrichedData);
    }

    public void deleteReview(Long id) {
        String sql = "DELETE FROM Review WHERE id="+id;
        jdbcTemplate.update(sql);
    }

    public void updateReview(Review review) {
        String sql = "UPDATE Review SET review = ?, score = ? WHERE id = ?";
        jdbcTemplate.update(sql, review.getReview(), review.getScore(), review.getBookId());
    }
}
