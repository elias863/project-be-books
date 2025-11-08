package com.iliad.library.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseUpdate{

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void alterMyTableAddMyColumn() {

        String query = "ALTER TABLE books CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci";
        entityManager.createNativeQuery(query).executeUpdate();
    }
}