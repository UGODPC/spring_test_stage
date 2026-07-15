package com.jiraws.library.book.persistence;

import com.jiraws.library.book.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    public BookEntity findByNameAndPages(String name, Integer pages);
}