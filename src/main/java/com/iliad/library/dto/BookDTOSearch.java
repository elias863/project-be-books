package com.iliad.library.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BookDTOSearch {
    private Long id;
    private String title;
    private List<PersonDTO> authors;
    private List<String> summaries;
    private List<PersonDTO> editors;
    private List<PersonDTO> translators;
    private List<String> subjects;
    private List<String> bookshelves;

    @JsonProperty("media_type")
    private String mediaType;
    private FormatDTO formats;
}
