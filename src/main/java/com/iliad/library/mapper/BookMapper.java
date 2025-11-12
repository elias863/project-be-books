package com.iliad.library.mapper;

import com.iliad.library.dto.BookDTO;
import com.iliad.library.dto.PersonDTO;
import com.iliad.library.dto.ReviewDTO;
import com.iliad.library.entity.Book;
import com.iliad.library.entity.Person;
import com.iliad.library.entity.Review;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class BookMapper {

    private final FormatMapper formatMapper;
    private final PersonMapper personMapper;
    private final ReviewMapper reviewMapper;

    public Book toEntity(BookDTO dto){
        Book entity = new Book();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());

        // mappo gli autori
        if(dto.getAuthors()!=null){
            List<Person> authors = new ArrayList<>(0);
            for(PersonDTO p:dto.getAuthors()){
                Person author = new Person();
                author.setName(p.getName());
                author.setBirthYear(p.getBirthYear());
                author.setDeathYear(p.getDeathYear());
                authors.add(author);
            }
            entity.setAuthors(authors);
        }
        else
            entity.setAuthors(new ArrayList<>(0));

        // mappo i summaries
        entity.setSummaries(dto.getSummaries());

        // mappo gli editors
        if(dto.getEditors()!=null){
            List<Person> editors = new ArrayList<>(0);
            for(PersonDTO t:dto.getEditors()){
                Person editor = new Person();
                editor.setName(t.getName());
                editor.setBirthYear(t.getBirthYear());
                editor.setDeathYear(t.getDeathYear());
                editors.add(editor);
            }
            entity.setEditors(editors);
        }
            else
                entity.setEditors(new ArrayList<>(0));

        // mappo i translators
        if(dto.getTranslators()!=null){
            List<Person> translators = new ArrayList<>(0);
            for(PersonDTO t:dto.getTranslators()){
                Person translator = new Person();
                translator.setName(t.getName());
                translator.setBirthYear(t.getBirthYear());
                translator.setDeathYear(t.getDeathYear());
                translators.add(translator);
            }
            entity.setTranslators(translators);
        }
        else
            entity.setTranslators(new ArrayList<>(0));

        // mappo i subjects
        entity.setSubjects(dto.getSubjects());

        // mappo i bookshelves
        entity.setBookshelves(dto.getBookshelves());

        // mappo i languages
        entity.setLanguages(dto.getLanguages());

        // mappo il copyright
        entity.setCopyright(dto.getCopyright());

        // mappo il mediaType
        entity.setMediaType(dto.getMediaType());

        // mappo i formats
        entity.setFormats(formatMapper.toEntity(dto.getFormats()));

        // mappo il downloadCount
        entity.setDownloadCount(dto.getDownloadCount());

        return entity;
    }

    public BookDTO toDto(Book entity){
        BookDTO dto = new BookDTO();
        dto.setId(entity.getId());

        // mappo gli editors
        List<PersonDTO> editors = new ArrayList<>(0);
        for(Person p:entity.getEditors()){
            PersonDTO personDTO = personMapper.toDto(p);
            editors.add(personDTO);
        }
        dto.setEditors(dto.getEditors());   // li aggiungo all'entity

        // mappo gli authors
        List<PersonDTO> authors = new ArrayList<>(0);
        for(Person p:entity.getAuthors()){
            PersonDTO personDTO = personMapper.toDto(p);
            authors.add(personDTO);
        }
        dto.setAuthors(authors);

        // mappo i summaries
        dto.setSummaries(entity.getSummaries());

        // mappo i translators
        List<PersonDTO> translators = new ArrayList<>(0);
        for(Person p:entity.getTranslators()){
            PersonDTO personDTO = personMapper.toDto(p);
            translators.add(personDTO);
        }
        dto.setTranslators(translators);

        dto.setSubjects(entity.getSubjects());
        dto.setBookshelves(entity.getBookshelves());
        dto.setLanguages(entity.getLanguages());
        dto.setCopyright(entity.getCopyright());
        dto.setMediaType(entity.getMediaType());
        dto.setFormats(formatMapper.toDto(entity.getFormats()));
        dto.setDownloadCount(entity.getDownloadCount());

        // mappo le Reviews
        List<ReviewDTO> reviews = new ArrayList<>(0);
        for(Review r:entity.getReviews()){
            ReviewDTO reviewDTO = reviewMapper.toDto(r);
            reviews.add(reviewDTO);
        }
        dto.setReviews(reviews);

        return dto;
    }
}
