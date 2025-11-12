package com.iliad.library.service;

import com.iliad.library.config.RabbitMQConfig;
import com.iliad.library.dto.BookDTOReview;
import com.iliad.library.dto.PersonDTO;
import com.iliad.library.dto.ReviewDTO;
import com.iliad.library.entity.Book;
import com.iliad.library.entity.Format;
import com.iliad.library.entity.Person;
import com.iliad.library.entity.Review;
import com.iliad.library.mapper.FormatMapper;
import com.iliad.library.mapper.PersonMapper;
import com.iliad.library.mapper.ReviewMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final BookService bookService;
    private final RabbitTemplate rabbitTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final PersonMapper personMapper;
    private final FormatMapper formatMapper;

    public ReviewDTO createReview(ReviewDTO reviewDTO) throws Exception {

        // controllo che l'id matchi con l'API (se non esiste lancia un'eccezione)
        Book reviewbook = new Book();

        try {
            reviewbook = bookService.getBookById(reviewDTO.getId());
        }catch (Exception e){   // se non esiste un libro con l'id scelto, lancia un eccezione al controller
            throw new Exception("Non esiste un libro con id = "+reviewDTO.getId());
        }

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
        String sql = "SELECT * FROM Book WHERE bookId = "+saved.getBookId();
        reviews = jdbcTemplate.query(sql,new BeanPropertyRowMapper(Review.class));

        // se il libro esiste già...
        if(!reviews.isEmpty()){ // aggiorno soltanto il campo STATUS (gli altri dati di arricchimento per questo libro sono già presenti)
            // prelevo l'id del libro già esistente e lo uso per settare la chiave esterna della review
            sql = "SELECT id FROM Book WHERE bookId = "+saved.getBookId();
            Long bookId = jdbcTemplate.queryForObject(sql, Long.class);

            // inserisco soltanto la review con il riferimento (lastBookIdSaved) al libro già esistente
            sql = "INSERT INTO Review (bookId, review, score, status, book_id) VALUES (?,?,?,?,?)";
            jdbcTemplate.update(sql, saved.getBookId(), saved.getReview(), saved.getScore(), "COMPLETED",bookId);

            // 0ttengo l'id della Review appena inserita per restituirlo al controller
            Long reviewId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            saved.setId(reviewId);
        }
        else{   // Altrimenti creo il libro e la Review di base
            // Creo il libro (necessario per creare la Review)
            sql = "INSERT INTO Book (title, copyright, mediaType, " +
                    "downloadCount, bookId) VALUES (?,?,?,?,?)";
            jdbcTemplate.update(sql, reviewbook.getTitle(), reviewbook.getCopyright(),
                    reviewbook.getMediaType(), reviewbook.getDownloadCount(), saved.getBookId()
            );

            //  ottengo l'id del libro appena creato da impostare come chiave esterna nella Review
            sql = "SELECT id FROM Book WHERE bookId = "+saved.getBookId();
            Long bookId = jdbcTemplate.queryForObject(sql, Long.class);

            // salvo la review con il campo status=COMPLETED
            Review lastReview = reviewbook.getReviews().get(reviewbook.getReviews().size()-1);
            sql = "INSERT INTO Review (bookId, review, score, status, book_id) VALUES (?,?,?,?,?)";
            jdbcTemplate.update(sql, lastReview.getBookId(), lastReview.getReview(), lastReview.getScore(), "COMPLETED",bookId);

            // 0ttengo l'id della Review appena inserita per restituirlo al controller
            Long reviewId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
            saved.setId(reviewId);

            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, reviewbook);
        }

        return reviewMapper.toDto(saved);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void reviewBookInsertReview(Book reviewbook) {

        // prelevo l'id del libro appena creato
        String sql = "SELECT id FROM Book WHERE bookId = "+reviewbook.getId();
        Long bookId = jdbcTemplate.queryForObject(sql, Long.class);

        // Inserimento formats relativi al Book
        Format formats = reviewbook.getFormats();
        sql = "INSERT INTO Format (textHtml, applicationEpubZip, applicationMobiPocket" +
                ", textPlainAscii, textPlainUtf8, textHtmlCharsetUtf8, applicationRdfXml" +
                ", imageJpeg, applicationOctetStream, downloadCount, " +
                " " +
                "applicationPdf, applicationMsword, applicationPrsTei," +
                " textHtmlUsAScii, textPlain, textXRst, textHtmlCharsetIso8859, textPlainCharsetIso8859, " +
                " audioOgg, audioMp4, audioMpeg, applicationPrsTex ,book_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql, formats.getTextHtml(), formats.getApplicationEpubZip(), formats.getApplicationMobiPocket(),
                formats.getTextPlainAscii(), formats.getTextPlainUtf8(), formats.getTextHtmlCharsetUtf8(),
                formats.getApplicationRdfXml(), formats.getImageJpeg(), formats.getApplicationOctetStream()
                ,formats.getDownloadCount(),formats.getApplicationPdf(), formats.getApplicationMsword(),
                formats.getApplicationPrsTei(), formats.getTextHtmlUsAScii(), formats.getTextPlain(), formats.getTextXRst(), formats.getTextHtmlCharsetIso8859(),
                formats.getTextPlainCharsetIso8859(), formats.getAudioOgg(), formats.getAudioMp4(), formats.getAudioMpeg(),
                formats.getApplicationPrsTex(), bookId);

        // Inserimento authors
        List<Person> authors = reviewbook.getAuthors();
        for(Person a:authors){
            sql = "INSERT INTO Author (name, birthYear, deathYear) VALUES (?,?,?)";
            jdbcTemplate.update(sql,a.getName(), a.getBirthYear(), a.getDeathYear());
            Long lastAuthorId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            // Aggiungo i riferimenti nella tabella BookAuthor
            sql = "INSERT INTO BookAuthor (book_id, author_id) VALUES (?,?)";
            jdbcTemplate.update(sql, bookId, lastAuthorId);
        }

        // Inserimento summaries
        List<String> summaries = reviewbook.getSummaries();
        sql = "INSERT INTO Summary (summary, book_id) VALUES (?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String summary = summaries.get(i);
                ps.setString(1, summary);
                ps.setLong(2, bookId);          // --> DA SISTEMARE COME L'ALTRO
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
            jdbcTemplate.update(sql, bookId, lastEditorId);
        }

        // Inserimento translators
        List<Person> translators = reviewbook.getTranslators();
        for(Person a:translators){
            sql = "INSERT INTO Translator (name, birthYear, deathYear) VALUES (?,?,?)";
            jdbcTemplate.update(sql,a.getName(), a.getBirthYear(), a.getDeathYear());
            Long lastAuthorId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            // Aggiungo i riferimenti nella tabella BookAuthor
            sql = "INSERT INTO BookTranslator (book_id, translator_id) VALUES (?,?)";
            jdbcTemplate.update(sql, bookId, lastAuthorId);
        }

        //  Inserimento subjects
        List<String> subjects = reviewbook.getSubjects();
        sql = "INSERT INTO Subject (subject, book_id) VALUES (?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String subject = subjects.get(i);
                ps.setString(1, subject);
                ps.setLong(2, bookId);
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
            jdbcTemplate.update(sql, bookId, lastBookShelfId);
        }

        // Inserimento languages
        List<String> languages = reviewbook.getLanguages();
        for(String l:languages){
            sql = "INSERT INTO Language (language) VALUES (?)";
            jdbcTemplate.update(sql,l);
            Long lastLanguageId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            // Aggiungo i riferimenti nella tabella BookAuthor
            sql = "INSERT INTO BookLanguage (book_id, language_id) VALUES (?,?)";
            jdbcTemplate.update(sql, bookId, lastLanguageId);
        }
    }

    // Ottengo tutte le review di un dato BookId preso in input
    public BookDTOReview getReview(Long id) throws Exception {

        // Prelevo tutte le review dal DB (in realtà è solo una) e la salvo in fromDB
        List<Review> reviewList = jdbcTemplate.query(
                "SELECT * FROM Review WHERE id = "+id,
                new Object[]{},
                new RowMapper() {
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Review review = new Review();

                        review.setId(rs.getLong("id"));
                        review.setReview(rs.getString("review"));
                        review.setScore(rs.getInt("score"));
                        review.setStatus(rs.getString("status"));
                        review.setBookId(rs.getLong("bookId"));

                        return review;
                    }
                }
        );

        Review fromDB = reviewList.get(0);

        // L'arricchimento della review è uguale per tutte dato che si riferiscono allo stesso libro
        Book enrichedData = bookService.getBookById(fromDB.getBookId());

        // mappo a DTO tutte le Review prima di passarle al Controller
        List<Review> newReviewList = new ArrayList<>();
        newReviewList.add(fromDB);

        enrichedData.setReviews(newReviewList);

        // mappo il book con bookDTOReview (la review con l'arricchimento dati del Book con i soli campi interessanti per l'output)
        BookDTOReview bookDTOReview = new BookDTOReview();

        // mappo le Review in ReviewDTO
        List<ReviewDTO> reviewDTOS = new ArrayList<>(0);
        for(Review r:enrichedData.getReviews()){
            ReviewDTO reviewDTO = reviewMapper.toDto(r);
            reviewDTOS.add(reviewDTO);
        }

        // continuo il mapping per l'output...
        bookDTOReview.setReview(reviewDTOS);
        bookDTOReview.setTitle(enrichedData.getTitle());

        // mappo gli authors
        List<PersonDTO> authors = new ArrayList<>(0);
        for(Person p:enrichedData.getAuthors()){
            PersonDTO author = personMapper.toDto(p);
            authors.add(author);
        }

        bookDTOReview.setAuthors(authors);
        bookDTOReview.setSummaries(enrichedData.getSummaries());

        // mappo gli editors
        List<PersonDTO> editors = new ArrayList<>(0);
        for(Person p:enrichedData.getEditors()){
            PersonDTO editor = personMapper.toDto(p);
            editors.add(editor);
        }

        bookDTOReview.setEditors(editors);

        // mappoo i translators
        List<PersonDTO> translators = new ArrayList<>(0);
        for(Person p:enrichedData.getTranslators()){
            PersonDTO translator = personMapper.toDto(p);
            translators.add(translator);
        }

        bookDTOReview.setTranslators(translators);
        bookDTOReview.setSubjects(enrichedData.getSubjects());
        bookDTOReview.setBookshelves(enrichedData.getBookshelves());
        bookDTOReview.setLanguages(enrichedData.getLanguages());
        bookDTOReview.setCopyright(enrichedData.getCopyright());
        bookDTOReview.setFormats(formatMapper.toDto(enrichedData.getFormats()));
        bookDTOReview.setMediaType(enrichedData.getMediaType());
        bookDTOReview.setDownloadCount(enrichedData.getDownloadCount());

        return bookDTOReview;
    }

    public int deleteReview(Long id) {
        String sql = "DELETE FROM Review WHERE id="+id;
        int check = jdbcTemplate.update(sql); // cancellato = 1, non cancellato = 0
        return check;
    }

    public int updateReview(Review review) {
        String sql = "UPDATE Review SET review = ?, score = ? WHERE id = ?";
        int check = jdbcTemplate.update(sql, review.getReview(), review.getScore(), review.getBookId());
        return check;
    }
}
