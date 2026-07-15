package com.jiraws.library.book.service;

import com.jiraws.library.book.persistence.BookRepository;
import com.jiraws.library.book.model.BookEntity;
import io.micrometer.common.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class BookService
{
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository)
    {
        this.bookRepository = bookRepository;
    }

    public String createBook(String bookName, Integer bookPages)
    {
        if(bookName == null || StringUtils.isBlank(bookName))
        {
            return("Le nom du livre n'a pas de nom... Donnez-en un !");
        }
        if(bookPages == null || bookPages <= 0)
        {
            return("Votre livre doit contenir au moins 1 page.");
        }
        BookEntity existingBook = bookRepository.findByNameAndPages(bookName, bookPages);

        if(existingBook == null)
        {
            BookEntity newBook = BookEntity.builder()
                    .name(bookName)
                    .pages(bookPages)
                    .build();

            bookRepository.save(newBook);

            return("Le livre à bien été créé !");
        }
        else
        {
            return("Le livre existe déjà !");
        }
    }

    public List<BookEntity> getAllBooks()
    {
        return bookRepository.findAll();
    }

    public BookEntity updateBook(Long id, String bookName, Integer bookPages)
    {

        BookEntity book = bookRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        book.setName(bookName);
        book.setPages(bookPages);

        bookRepository.save(book);

        return(book);
    }
    public ResponseEntity<BookEntity> getBookById(@PathVariable Long id)
    {
        BookEntity book = bookRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        return ResponseEntity.ok(book);
    }

    public String deleteBook(Long id)
    {
        BookEntity book = bookRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        bookRepository.delete(book);

        return("Le livre a bien été supprimé");
    }
}
