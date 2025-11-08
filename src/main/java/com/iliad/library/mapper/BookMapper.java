package com.iliad.library.mapper;

import com.iliad.library.dto.BookDTO;
import com.iliad.library.dto.PersonDTO;
import com.iliad.library.dto.ReviewDTO;
import com.iliad.library.entity.Book;
import com.iliad.library.entity.Person;
import com.iliad.library.entity.Review;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class BookMapper {

    private final FormatMapper formatMapper;

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
        entity.setEditors(dto.getEditors());

        // mappo i translators
        List<Person> translators = new ArrayList<>(0);
        for(PersonDTO t:dto.getTranslators()){
            Person translator = new Person();
            translator.setName(t.getName());
            translator.setBirthYear(t.getBirthYear());
            translator.setDeathYear(t.getDeathYear());
            translators.add(translator);
        }
        entity.setTranslators(translators);

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

    public ReviewDTO toDto(Review entity){
        ReviewDTO dto = new ReviewDTO();
        dto.setId(entity.getId());
        dto.setReview(entity.getReview());
        dto.setScore(entity.getScore());
        return dto;
    }
}
