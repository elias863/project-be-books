package com.iliad.library.mapper;

import com.iliad.library.dto.PersonDTO;
import com.iliad.library.entity.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public Person toEntity(PersonDTO dto){
        Person entity = new Person();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDeathYear(dto.getDeathYear());
        entity.setBirthYear(dto.getBirthYear());
        return entity;
    }

    public PersonDTO toDto(Person entity){
        PersonDTO dto = new PersonDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDeathYear(entity.getDeathYear());
        dto.setBirthYear(entity.getBirthYear());
        return dto;
    }
}
