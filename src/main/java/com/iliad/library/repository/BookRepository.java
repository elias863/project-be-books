package com.iliad.library.repository;

import com.iliad.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
//    @Query(value = "ALTER TABLE books CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;", nativeQuery = true)
//    void changeEncoding();
}


