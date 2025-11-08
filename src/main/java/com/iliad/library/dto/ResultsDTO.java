package com.iliad.library.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResultsDTO {
    private int count;
    private String next;
    private String previous;
    private List<BookDTO> results;
}
