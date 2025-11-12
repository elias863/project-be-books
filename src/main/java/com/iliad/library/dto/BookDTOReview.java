package com.iliad.library.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BookDTOReview {

    private List<ReviewDTO> review;

    private String title;
    private List<PersonDTO> authors;
    private List<String> summaries;
    private List<PersonDTO> editors;
    private List<PersonDTO> translators;
    private List<String> subjects;
    private List<String> bookshelves;
    private List<String> languages;
    private Boolean copyright;
    private FormatDTO formats;
    @JsonProperty("media_type")
    private String mediaType;
    @JsonProperty("download_count")
    private int downloadCount;
}
