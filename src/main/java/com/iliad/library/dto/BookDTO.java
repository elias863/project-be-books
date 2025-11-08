package com.iliad.library.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private List<PersonDTO> authors;
    private List<String> summaries;
    private List<String> editors;
    private List<PersonDTO> translators;
    private List<String> subjects;
    private List<String> bookshelves;
    private List<String> languages;
    private Boolean copyright;

    @JsonProperty("media_type")
    private String mediaType;
    private FormatDTO formats;

    @JsonProperty("download_count")
    private int downloadCount;
}
