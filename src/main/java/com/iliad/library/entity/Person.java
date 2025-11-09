package com.iliad.library.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "Person")
public class Person {

    @Id
    private Long id;

    private String name;
    private int birthYear;
    private int deathYear;
}
