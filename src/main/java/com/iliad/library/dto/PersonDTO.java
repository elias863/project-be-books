package com.iliad.library.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PersonDTO {


    private Long id;
    private String name;

    @JsonProperty("birth_year")
    private int birthYear;

    @JsonProperty("death_year")
    private int deathYear;
}
